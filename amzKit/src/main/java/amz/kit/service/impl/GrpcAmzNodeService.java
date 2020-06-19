package amz.kit.service.impl;

import amz.kit.crypto.AmzCrypto;
import amz.kit.entity.*;
import amz.kit.entity.response.*;
import amz.kit.service.AmzApiException;
import amz.kit.service.AmzNodeService;
import amz.kit.service.impl.grpc.BrsApi;
import amz.kit.service.impl.grpc.BrsApiServiceGrpc;
import amz.kit.util.SchedulerAssigner;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.reactivex.Observable;
import io.reactivex.Single;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public class GrpcAmzNodeService implements AmzNodeService {

    private final BrsApiServiceGrpc.BrsApiServiceBlockingStub brsGrpc;
    private final SchedulerAssigner schedulerAssigner;

    public GrpcAmzNodeService(String nodeAddress, SchedulerAssigner schedulerAssigner) {
        if (nodeAddress.startsWith("grpc://")) nodeAddress = nodeAddress.substring(7);
        this.schedulerAssigner = schedulerAssigner;
        // TODO don't use plaintext
        this.brsGrpc = BrsApiServiceGrpc.newBlockingStub(ManagedChannelBuilder.forTarget(nodeAddress).usePlaintext().build());
    }

    private <T> Observable<T> assign(Iterator<T> iterable) {
        return Observable.fromIterable(() -> iterable)
                .onErrorResumeNext(t -> {
                    if (t instanceof StatusRuntimeException && ((StatusRuntimeException) t).getStatus().getCode() == Status.Code.ABORTED) {
                        return Observable.error(new AmzApiException(((StatusRuntimeException) t).getStatus().getDescription()));
                    } else {
                        return Observable.error(t);
                    }
                });
    }

    private <T> Single<T> assign(Callable<T> callable) {
        return assign(Single.fromCallable(callable))
                .onErrorResumeNext(t -> {
                    if (t instanceof StatusRuntimeException && ((StatusRuntimeException) t).getStatus().getCode() == Status.Code.ABORTED) {
                        return Single.error(new AmzApiException(((StatusRuntimeException) t).getStatus().getDescription()));
                    } else {
                        return Single.error(t);
                    }
                });
    }

    private <T> Single<T> assign(Single<T> single) {
        return schedulerAssigner.assignSchedulers(single);
    }

    private final BrsApi.GetAccountRequest getAccountRequestFromId(AmzAddress id) {
        return BrsApi.GetAccountRequest.newBuilder()
                .setAccountId(id.getAmzID().getSignedLongId())
                .build();
    }

    private final BrsApi.GetByIdRequest getByIdRequestFromId(AmzID id) {
        return BrsApi.GetByIdRequest.newBuilder()
                .setId(id.getSignedLongId())
                .build();
    }

    @Override
    public Single<Block> getBlock(AmzID block) {
        return assign(() -> brsGrpc.getBlock(
                BrsApi.GetBlockRequest.newBuilder()
                        .setBlockId(block.getSignedLongId())
                        .build()))
                .map(Block::new);
    }

    @Override
    public Single<Block> getBlock(int height) {
        return assign(() -> brsGrpc.getBlock(
                BrsApi.GetBlockRequest.newBuilder()
                        .setHeight(height)
                        .build()))
                .map(Block::new);
    }

    @Override
    public Single<Block> getBlock(AmzTimestamp timestamp) {
        return assign(() -> brsGrpc.getBlock(
                BrsApi.GetBlockRequest.newBuilder()
                        .setTimestamp(timestamp.getTimestamp())
                        .build()))
                .map(Block::new);
    }

    @Override
    public Single<AmzID> getBlockId(int height) {
        return getBlock(height)
                .map(Block::getId);
    }

    @Override
    public Single<Block[]> getBlocks(int firstIndex, int lastIndex) {
        return assign(() -> brsGrpc.getBlocks(
                BrsApi.GetBlocksRequest.newBuilder()
                        .setIncludeTransactions(false)
                        .setIndexRange(
                                BrsApi.IndexRange.newBuilder()
                                        .setFirstIndex(firstIndex)
                                        .setLastIndex(lastIndex)
                                        .build())
                        .build()))
                .map(blocks -> blocks.getBlocksList()
                        .stream()
                        .map(Block::new)
                        .toArray(Block[]::new));
    }

    @Override
    public Single<Constants> getConstants() {
        return assign(() -> brsGrpc.getConstants(Empty.getDefaultInstance()))
                .map(Constants::new);
    }

    @Override
    public Single<Account> getAccount(AmzAddress accountId) {
        return assign(() -> brsGrpc.getAccount(getAccountRequestFromId(accountId)))
                .map(Account::new);
    }

    @Override
    public Single<AT[]> getAccountATs(AmzAddress accountId) {
        return assign(() -> brsGrpc.getAccountATs(getAccountRequestFromId(accountId)))
                .map(accountATs -> accountATs.getAtsList()
                        .stream()
                        .map(AT::new)
                        .toArray(AT[]::new));
    }

    @Override
    public Single<AmzID[]> getAccountBlockIDs(AmzAddress accountId) {
        return getAccountBlocks(accountId)
                .map(blocks -> Arrays.stream(blocks)
                        .map(Block::getId)
                        .toArray(AmzID[]::new));
    }

    @Override
    public Single<Block[]> getAccountBlocks(AmzAddress accountId) {
        return assign(() -> brsGrpc.getAccountBlocks(
                BrsApi.GetAccountBlocksRequest.newBuilder()
                        .setAccountId(accountId.getAmzID().getSignedLongId())
                        .setIncludeTransactions(false)
                        .build()))
                .map(accountATs -> accountATs.getBlocksList()
                        .stream()
                        .map(Block::new)
                        .toArray(Block[]::new));
    }

    @Override
    public Single<AmzID[]> getAccountTransactionIDs(AmzAddress accountId) { // TODO should this be deprecated?
        return getAccountTransactions(accountId)
                .map(transactions -> Arrays.stream(transactions)
                        .map(Transaction::getId)
                        .toArray(AmzID[]::new));
    }

    @Override
    public Single<Transaction[]> getAccountTransactions(AmzAddress accountId) {
        return assign(() -> brsGrpc.getAccountTransactions(
                BrsApi.GetAccountTransactionsRequest.newBuilder()
                        .setAccountId(accountId.getAmzID().getSignedLongId())
                        .build()))
                .map(transactions -> transactions.getTransactionsList()
                        .stream()
                        .map(Transaction::new)
                        .toArray(Transaction[]::new));
    }

    @Override
    public Single<AmzAddress[]> getAccountsWithRewardRecipient(AmzAddress accountId) {
        return assign(() -> brsGrpc.getAccounts(
                BrsApi.GetAccountsRequest.newBuilder()
                        .setRewardRecipient(accountId.getAmzID().getSignedLongId())
                        .setIncludeAccounts(false)
                        .build()))
                .map(accounts -> accounts.getIdsList()
                        .stream()
                        .map(AmzAddress::fromId)
                        .toArray(AmzAddress[]::new));
    }

    @Override
    public Single<AT> getAt(AmzAddress atId) {
        return assign(() -> brsGrpc.getAT(getByIdRequestFromId(atId.getAmzID())))
                .map(AT::new);
    }

    @Override
    public Single<AmzAddress[]> getAtIds() {
        return assign(() -> brsGrpc.getATIds(Empty.getDefaultInstance()))
                .map(atIds -> atIds.getIdsList()
                        .stream()
                        .map(AmzAddress::fromId)
                        .toArray(AmzAddress[]::new));
    }

    @Override
    public Single<Transaction> getTransaction(AmzID transactionId) {
        return assign(() -> brsGrpc.getTransaction(
                BrsApi.GetTransactionRequest.newBuilder()
                        .setTransactionId(transactionId.getSignedLongId())
                        .build()))
                .map(Transaction::new);
    }

    @Override
    public Single<Transaction> getTransaction(byte[] fullHash) {
        return assign(() -> brsGrpc.getTransaction(
                BrsApi.GetTransactionRequest.newBuilder()
                        .setFullHash(ByteString.copyFrom(fullHash))
                        .build()))
                .map(Transaction::new);
    }

    @Override
    public Single<byte[]> getTransactionBytes(AmzID transactionId) { // TODO should this be deprecated?
        return assign(() -> brsGrpc.getTransaction(
                BrsApi.GetTransactionRequest.newBuilder()
                        .setTransactionId(transactionId.getSignedLongId())
                        .build()))
                .map(transaction -> brsGrpc.getTransactionBytes(transaction.getTransaction()))
                .map(transactionBytes -> transactionBytes.getTransactionBytes().toByteArray());
    }

    private Single<Any> ordinaryTransactionOrArbitraryMessage(AmzValue amount) {
        return Single.fromCallable(() -> {
            if (amount.equals(AmzValue.ZERO)) {
                return Any.pack(BrsApi.ArbitraryMessageAttachment.getDefaultInstance());
            } else {
                return Any.pack(BrsApi.OrdinaryPaymentAttachment.getDefaultInstance());
            }
        });
    }

    private BrsApi.BasicTransaction.Builder basicTransaction(byte[] senderPublicKey, AmzValue amount, AmzValue fee, int deadline, Any attachment) {
        AmzCrypto amzCrypto = AmzCrypto.getInstance();
        return BrsApi.BasicTransaction.newBuilder()
                .setSenderPublicKey(ByteString.copyFrom(senderPublicKey))
                .setSenderId(amzCrypto.getAmzAddressFromPublic(senderPublicKey).getSignedLongId())
                .setAmount(amount.toPlanck().longValueExact())
                .setFee(fee.toPlanck().longValueExact())
                .setDeadline(deadline)
                .setAttachment(attachment);
    }

    private BrsApi.BasicTransaction.Builder basicTransaction(AmzAddress recipient, byte[] senderPublicKey, AmzValue amount, AmzValue fee, int deadline, Any attachment) {
        return basicTransaction(senderPublicKey, amount, fee, deadline, attachment)
                .setRecipient(recipient.getSignedLongId());
    }

    @Override
    public Single<byte[]> generateTransaction(AmzAddress recipient, byte[] senderPublicKey, AmzValue amount, AmzValue fee, int deadline) {
        return ordinaryTransactionOrArbitraryMessage(amount)
                .map(attachment -> basicTransaction(recipient, senderPublicKey, amount, fee, deadline, attachment)
                        .build())
                .map(brsGrpc::completeBasicTransaction)
                .map(brsGrpc::getTransactionBytes)
                .map(bytes -> bytes.getTransactionBytes().toByteArray());
    }

    @Override
    public Single<byte[]> generateTransactionWithMessage(AmzAddress recipient, byte[] senderPublicKey, AmzValue amount, AmzValue fee, int deadline, String message) {
        return ordinaryTransactionOrArbitraryMessage(amount)
                .map(attachment -> basicTransaction(recipient, senderPublicKey, amount, fee, deadline, attachment)
                        .addAppendages(Any.pack(BrsApi.MessageAppendix.newBuilder()
                                .setMessage(ByteString.copyFrom(message.getBytes(StandardCharsets.UTF_8)))
                                .setIsText(true)
                                .build()))
                        .build())
                .map(brsGrpc::completeBasicTransaction)
                .map(brsGrpc::getTransactionBytes)
                .map(bytes -> bytes.getTransactionBytes().toByteArray());
    }

    @Override
    public Single<byte[]> generateTransactionWithMessage(AmzAddress recipient, byte[] senderPublicKey, AmzValue amount, AmzValue fee, int deadline, byte[] message) {
        return ordinaryTransactionOrArbitraryMessage(amount)
                .map(attachment -> basicTransaction(recipient, senderPublicKey, amount, fee, deadline, attachment)
                        .addAppendages(Any.pack(BrsApi.MessageAppendix.newBuilder()
                                .setMessage(ByteString.copyFrom(message))
                                .setIsText(false)
                                .build()))
                        .build())
                .map(brsGrpc::completeBasicTransaction)
                .map(brsGrpc::getTransactionBytes)
                .map(bytes -> bytes.getTransactionBytes().toByteArray());
    }

    @Override
    public Single<byte[]> generateTransactionWithEncryptedMessage(AmzAddress recipient, byte[] senderPublicKey, AmzValue amount, AmzValue fee, int deadline, AmzEncryptedMessage message) {
        return ordinaryTransactionOrArbitraryMessage(amount)
                .map(attachment -> basicTransaction(recipient, senderPublicKey, amount, fee, deadline, attachment)
                        .addAppendages(Any.pack(BrsApi.EncryptedMessageAppendix.newBuilder()
                                .setEncryptedData(BrsApi.EncryptedData.newBuilder()
                                        .setData(ByteString.copyFrom(message.getData()))
                                        .setNonce(ByteString.copyFrom(message.getNonce()))
                                        .build())
                                .setIsText(message.isText())
                                .setType(BrsApi.EncryptedMessageAppendix.Type.TO_RECIPIENT)
                                .build()))
                        .build())
                .map(brsGrpc::completeBasicTransaction)
                .map(brsGrpc::getTransactionBytes)
                .map(bytes -> bytes.getTransactionBytes().toByteArray());
    }

    @Override
    public Single<byte[]> generateTransactionWithEncryptedMessageToSelf(AmzAddress recipient, byte[] senderPublicKey, AmzValue amount, AmzValue fee, int deadline, AmzEncryptedMessage message) {
        return ordinaryTransactionOrArbitraryMessage(amount)
                .map(attachment -> basicTransaction(recipient, senderPublicKey, amount, fee, deadline, attachment)
                        .addAppendages(Any.pack(BrsApi.EncryptedMessageAppendix.newBuilder()
                                .setEncryptedData(BrsApi.EncryptedData.newBuilder()
                                        .setData(ByteString.copyFrom(message.getData()))
                                        .setNonce(ByteString.copyFrom(message.getNonce()))
                                        .build())
                                .setIsText(message.isText())
                                .setType(BrsApi.EncryptedMessageAppendix.Type.TO_SELF)
                                .build()))
                        .build())
                .map(brsGrpc::completeBasicTransaction)
                .map(brsGrpc::getTransactionBytes)
                .map(bytes -> bytes.getTransactionBytes().toByteArray());
    }

    @Override
    public Single<FeeSuggestion> suggestFee() {
        return assign(() -> brsGrpc.suggestFee(Empty.getDefaultInstance()))
                .map(FeeSuggestion::new);
    }

    @Override
    public Observable<MiningInfo> getMiningInfo() {
        return assign(brsGrpc.getMiningInfo(Empty.getDefaultInstance()))
                .map(MiningInfo::new);
    }

    @Override
    public Single<TransactionBroadcast> broadcastTransaction(byte[] transactionBytes) {
        return assign(() -> brsGrpc.broadcastTransactionBytes(BrsApi.TransactionBytes.newBuilder()
                .setTransactionBytes(ByteString.copyFrom(transactionBytes))
                .build()))
                .map(result -> new TransactionBroadcast(result, transactionBytes));
    }

    @Override
    public Single<AmzAddress> getRewardRecipient(AmzAddress address) {
        return assign(() -> brsGrpc.getAccount(getAccountRequestFromId(address)))
                .map(account -> AmzAddress.fromId(account.getRewardRecipient()));
    }

    @Override
    public Single<Long> submitNonce(String passphrase, String nonce, AmzID accountId) {
        return assign(() -> brsGrpc.submitNonce(BrsApi.SubmitNonceRequest.newBuilder()
                .setSecretPhrase(passphrase)
                .setAccount(accountId == null ? 0 : accountId.getSignedLongId())
                .setNonce(Long.parseUnsignedLong(nonce))
                .build()))
                .map(BrsApi.SubmitNonceResponse::getDeadline);
    }

    @Override
    public Single<byte[]> generateMultiOutTransaction(byte[] senderPublicKey, AmzValue fee, int deadline, Map<AmzAddress, AmzValue> recipients) throws IllegalArgumentException {
        return Single.fromCallable(() -> {
            AmzValue totalValue = AmzValue.ZERO;
            for (AmzValue value : recipients.values()) {
                totalValue = totalValue.add(value);
            }
            return totalValue;
        }).map(totalValue -> basicTransaction(senderPublicKey, totalValue, fee, deadline, Any.pack(BrsApi.MultiOutAttachment.newBuilder()
                .setVersion(1)
                .addAllRecipients(() -> recipients.entrySet().stream()
                        .map(entry -> BrsApi.MultiOutAttachment.MultiOutRecipient.newBuilder()
                                .setRecipient(entry.getKey().getSignedLongId())
                                .setAmount(entry.getValue().toPlanck().longValueExact())
                                .build())
                        .iterator()).build()))
                .build())
                .map(brsGrpc::completeBasicTransaction)
                .map(brsGrpc::getTransactionBytes)
                .map(bytes -> bytes.getTransactionBytes().toByteArray());
    }

    @Override
    public Single<byte[]> generateMultiOutSameTransaction(byte[] senderPublicKey, AmzValue amount, AmzValue fee, int deadline, Set<AmzAddress> recipients) throws IllegalArgumentException {
        return Single.fromCallable(() -> basicTransaction(senderPublicKey, amount, fee, deadline, Any.pack(BrsApi.MultiOutSameAttachment.newBuilder()
                .setVersion(1)
                .addAllRecipients(() -> recipients.stream()
                        .map(AmzAddress::getSignedLongId)
                        .iterator()).build()))
                .build())
                .map(brsGrpc::completeBasicTransaction)
                .map(brsGrpc::getTransactionBytes)
                .map(bytes -> bytes.getTransactionBytes().toByteArray());
    }

    @Override
    public Single<byte[]> generateCreateATTransaction(byte[] senderPublicKey, AmzValue fee, int deadline, String name, String description, byte[] creationBytes) {
        return Single.fromCallable(() -> basicTransaction(senderPublicKey, AmzValue.ZERO, fee, deadline, Any.pack(BrsApi.ATCreationAttachment.newBuilder()
                .setVersion(1)
                .setName(name)
                .setDescription(description)
                .setCreationBytes(ByteString.copyFrom(creationBytes))
                .build()))
                .build())
                .map(brsGrpc::completeBasicTransaction)
                .map(brsGrpc::getTransactionBytes)
                .map(bytes -> bytes.getTransactionBytes().toByteArray());
    }
}
