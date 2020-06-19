package amz.kit.service.impl;

import amz.kit.entity.*;
import amz.kit.entity.response.*;
import amz.kit.service.AmzNodeService;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompositeAmzNodeService implements AmzNodeService {
    private final AmzNodeService[] amzNodeServices;

    /**
     * @param amzNodeServices The amz node services this will wrap, in order of priority
     */
    public CompositeAmzNodeService(AmzNodeService... amzNodeServices) {
        if (amzNodeServices == null || amzNodeServices.length == 0) throw new IllegalArgumentException("No Amz Node Services Provided");
        this.amzNodeServices = amzNodeServices;
    }

    private <T> Single<T> compositeSingle(Collection<Single<T>> singles) {
        return Single.create(emitter -> {
            AtomicInteger errorCount = new AtomicInteger(0);
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            emitter.setCancellable(compositeDisposable::dispose);
            for (Single<T> single : singles) {
                compositeDisposable.add(single.subscribe(emitter::onSuccess, error -> {
                    synchronized (errorCount) {
                        if (errorCount.incrementAndGet() == singles.size()) { // Every single has errored
                            emitter.onError(error);
                        }
                    }
                }));
            }
        });
    }

    private synchronized <T> void doIfUsedObservable(ObservableEmitter<T> emitter, AtomicInteger usedObservable, AtomicReferenceArray<Disposable> disposables, int myI, Runnable runnable) {
        int used = usedObservable.get();
        if (used == -1) {
            // We are the first!
            usedObservable.set(myI);
            runnable.run();
            // Kill all of the others.
            Disposable myDisposable = disposables.get(myI);
            disposables.set(myI, null);
            emitter.setCancellable(() -> {
                if (myDisposable != null) {
                    myDisposable.dispose();
                }
            }); // Calling this calls the previous one, so all of the others get disposed.
        } else if (used == myI) {
            // We are the used observable.
            runnable.run();
        }
    }

    private <T> Observable<T> compositeObservable(List<Observable<T>> observables) {
        return Observable.create(emitter -> {
            AtomicInteger usedObservable = new AtomicInteger(-1);
            AtomicInteger errorCount = new AtomicInteger(0);
            AtomicReferenceArray<Disposable> disposables = new AtomicReferenceArray<>(observables.size());
            emitter.setCancellable(() -> {
                for (int i = 0; i < disposables.length(); i++) {
                    Disposable disposable = disposables.get(i);
                    if (disposable != null) disposable.dispose();
                }
            });
            for (int i = 0; i < observables.size(); i++) {
                final int myI = i;
                Observable<T> observable = observables.get(i);
                disposables.set(myI, observable.subscribe(t -> doIfUsedObservable(emitter, usedObservable, disposables, myI, () -> emitter.onNext(t)),
                        error -> {
                            synchronized (errorCount) {
                                if (errorCount.incrementAndGet() == observables.size() || usedObservable.get() == myI) { // Every single has errored
                                    emitter.onError(error);
                                }
                            }
                        },
                        () -> doIfUsedObservable(emitter, usedObservable, disposables, myI, emitter::onComplete)));
            }
        });
    }

    private <T, U> List<U> map(T[] ts, Function<T, U> mapper) {
        return Arrays.stream(ts)
                .map(mapper)
                .collect(Collectors.toList());
    }

    private <T> Single<T> performFastest(Function<AmzNodeService, Single<T>> function) {
        return compositeSingle(map(amzNodeServices, function));
    }

    private <T> Observable<T> performFastestObservable(Function<AmzNodeService, Observable<T>> function) {
        return compositeObservable(map(amzNodeServices, function));
    }

    private <T> Single<T> performOnOne(Function<AmzNodeService, Single<T>> function) {
        List<Single<T>> singles = map(amzNodeServices, function);
        for (int i = singles.size() - 2; i >= 0; i--) {
            singles.set(i, singles.get(i).onErrorResumeNext(singles.get(i+1)));
        }
        return singles.get(0);
    }

    @Override
    public Single<Block> getBlock(AmzID block) {
        return performFastest(service -> service.getBlock(block));
    }

    @Override
    public Single<Block> getBlock(int height) {
        return performFastest(service -> service.getBlock(height));
    }

    @Override
    public Single<Block> getBlock(AmzTimestamp timestamp) {
        return performFastest(service -> service.getBlock(timestamp));
    }

    @Override
    public Single<AmzID> getBlockId(int height) {
        return performFastest(service -> service.getBlockId(height));
    }

    @Override
    public Single<Block[]> getBlocks(int firstIndex, int lastIndex) {
        return performFastest(service -> service.getBlocks(firstIndex, lastIndex));
    }

    @Override
    public Single<Constants> getConstants() {
        return performFastest(AmzNodeService::getConstants);
    }

    @Override
    public Single<Account> getAccount(AmzAddress accountId) {
        return performFastest(service -> service.getAccount(accountId));
    }

    @Override
    public Single<AT[]> getAccountATs(AmzAddress accountId) {
        return performFastest(service -> service.getAccountATs(accountId));
    }

    @Override
    public Single<AmzID[]> getAccountBlockIDs(AmzAddress accountId) {
        return performFastest(service -> service.getAccountBlockIDs(accountId));
    }

    @Override
    public Single<Block[]> getAccountBlocks(AmzAddress accountId) {
        return performFastest(service -> service.getAccountBlocks(accountId));
    }

    @Override
    public Single<AmzID[]> getAccountTransactionIDs(AmzAddress accountId) {
        return performFastest(service -> service.getAccountTransactionIDs(accountId));
    }

    @Override
    public Single<Transaction[]> getAccountTransactions(AmzAddress accountId) {
        return performFastest(service -> service.getAccountTransactions(accountId));
    }

    @Override
    public Single<AmzAddress[]> getAccountsWithRewardRecipient(AmzAddress accountId) {
        return performFastest(service -> service.getAccountsWithRewardRecipient(accountId));
    }

    @Override
    public Single<AT> getAt(AmzAddress at) {
        return performFastest(service -> service.getAt(at));
    }

    @Override
    public Single<AmzAddress[]> getAtIds() {
        return performFastest(service -> service.getAtIds());
    }

    @Override
    public Single<Transaction> getTransaction(AmzID transactionId) {
        return performFastest(service -> service.getTransaction(transactionId));
    }

    @Override
    public Single<Transaction> getTransaction(byte[] fullHash) {
        return performFastest(service -> service.getTransaction(fullHash));
    }

    @Override
    public Single<byte[]> getTransactionBytes(AmzID transactionId) {
        return performFastest(service -> service.getTransactionBytes(transactionId));
    }

    @Override
    public Single<byte[]> generateTransaction(AmzAddress recipient, byte[] senderPublicKey, AmzValue amount, AmzValue fee, int deadline) {
        return performFastest(service -> service.generateTransaction(recipient, senderPublicKey, amount, fee, deadline));
    }

    @Override
    public Single<byte[]> generateTransactionWithMessage(AmzAddress recipient, byte[] senderPublicKey, AmzValue amount, AmzValue fee, int deadline, String message) {
        return performFastest(service -> service.generateTransactionWithMessage(recipient, senderPublicKey, amount, fee, deadline, message));
    }

    @Override
    public Single<byte[]> generateTransactionWithMessage(AmzAddress recipient, byte[] senderPublicKey, AmzValue amount, AmzValue fee, int deadline, byte[] message) {
        return performFastest(service -> service.generateTransactionWithMessage(recipient, senderPublicKey, amount, fee, deadline, message));
    }

    @Override
    public Single<byte[]> generateTransactionWithEncryptedMessage(AmzAddress recipient, byte[] senderPublicKey, AmzValue amount, AmzValue fee, int deadline, AmzEncryptedMessage message) {
        return performFastest(service -> service.generateTransactionWithEncryptedMessage(recipient, senderPublicKey, amount, fee, deadline, message));
    }

    @Override
    public Single<byte[]> generateTransactionWithEncryptedMessageToSelf(AmzAddress recipient, byte[] senderPublicKey, AmzValue amount, AmzValue fee, int deadline, AmzEncryptedMessage message) {
        return performFastest(service -> service.generateTransactionWithEncryptedMessageToSelf(recipient, senderPublicKey, amount, fee, deadline, message));
    }

    @Override
    public Single<FeeSuggestion> suggestFee() {
        return performFastest(AmzNodeService::suggestFee);
    }

    @Override
    public Observable<MiningInfo> getMiningInfo() {
        return performFastestObservable(AmzNodeService::getMiningInfo);
    }

    @Override
    public Single<TransactionBroadcast> broadcastTransaction(byte[] transactionBytes) {
        return performFastest(service -> service.broadcastTransaction(transactionBytes));
    }

    @Override
    public Single<AmzAddress> getRewardRecipient(AmzAddress account) {
        return performFastest(service -> service.getRewardRecipient(account));
    }

    @Override
    public Single<Long> submitNonce(String passphrase, String nonce, AmzID accountId) {
        return performOnOne(service -> service.submitNonce(passphrase, nonce, accountId));
    }

    @Override
    public Single<byte[]> generateMultiOutTransaction(byte[] senderPublicKey, AmzValue fee, int deadline, Map<AmzAddress, AmzValue> recipients) throws IllegalArgumentException {
        return performFastest(service -> service.generateMultiOutTransaction(senderPublicKey, fee, deadline, recipients));
    }

    @Override
    public Single<byte[]> generateMultiOutSameTransaction(byte[] senderPublicKey, AmzValue amount, AmzValue fee, int deadline, Set<AmzAddress> recipients) throws IllegalArgumentException {
        return performFastest(service -> service.generateMultiOutSameTransaction(senderPublicKey, amount, fee, deadline, recipients));
    }

    @Override
    public Single<byte[]> generateCreateATTransaction(byte[] senderPublicKey, AmzValue fee, int deadline, String name, String description, byte[] creationBytes) {
        return performFastest(service -> service.generateCreateATTransaction(senderPublicKey, fee, deadline, name, description, creationBytes));
    }
}
