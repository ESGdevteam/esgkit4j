package amz.kit.service.impl;

import amz.kit.entity.*;
import amz.kit.entity.response.*;
import amz.kit.entity.response.http.*;
import amz.kit.service.AmzApiException;
import amz.kit.service.AmzNodeService;
import amz.kit.util.AmzKitUtils;
import amz.kit.util.SchedulerAssigner;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.OkHttpClient;
import org.bouncycastle.util.encoders.Hex;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public final class HttpAmzNodeService implements AmzNodeService {

    private final SchedulerAssigner schedulerAssigner;

    private BlockchainService blockchainService;

    public HttpAmzNodeService(String nodeAddress, String providedUserAgent, SchedulerAssigner schedulerAssigner) {
        this.schedulerAssigner = schedulerAssigner;

        String userAgent = providedUserAgent == null ? "amzkit4j/"+ amz.kit.Constants.VERSION : providedUserAgent;

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> chain.proceed(chain.request().newBuilder()
                        .header("User-Agent", userAgent)
                        .build()))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(nodeAddress)
                .addConverterFactory(GsonConverterFactory.create(AmzKitUtils.buildGson().create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        blockchainService = retrofit.create(BlockchainService.class);
    }
    
    private <T> Single<T> assign(Single<T> source) {
        return schedulerAssigner.assignSchedulers(source.map(this::checkBrsResponse));
    }

    private <T> Observable<T> assign(Observable<T> source) {
        return schedulerAssigner.assignSchedulers(source);
    }

    private <T> T checkBrsResponse(T source) throws BRSError {
        if (source instanceof BRSResponse) {
            ((BRSResponse) source).throwIfError();
        }
        return source;
    }

    @Override
    public Single<Block> getBlock(AmzID block) {
        return assign(blockchainService.getBlock(block.getID(), null, null, null))
                .map(Block::new);
    }

    @Override
    public Single<Block> getBlock(int height) {
        return assign(blockchainService.getBlock(null, String.valueOf(height), null, null))
                .map(Block::new);
    }

    @Override
    public Single<Block> getBlock(AmzTimestamp timestamp) {
        return assign(blockchainService.getBlock(null, null, String.valueOf(timestamp.getTimestamp()), null))
                .map(Block::new);
    }

    @Override
    public Single<AmzID> getBlockId(int height) {
        return assign(blockchainService.getBlockID(String.valueOf(height)))
                .map(response -> AmzID.fromLong(response.getBlockID()));
    }

    @Override
    public Single<Block[]> getBlocks(int firstIndex, int lastIndex) {
        return assign(blockchainService.getBlocks(String.valueOf(firstIndex), String.valueOf(lastIndex), null))
                .map(response -> Arrays.stream(response.getBlocks())
                        .map(Block::new)
                        .collect(Collectors.toList())
                        .toArray(new Block[0]));
    }

    @Override
    public Single<Constants> getConstants() {
        return assign(blockchainService.getConstants())
                .map(Constants::new);
    }

    @Override
    public Single<Account> getAccount(AmzAddress accountId) {
        return assign(blockchainService.getAccount(accountId.getID()))
                .map(Account::new);
    }

    @Override
    public Single<AT[]> getAccountATs(AmzAddress accountId) {
        return assign(blockchainService.getAccountATs(accountId.getID()))
                .map(response -> Arrays.stream(response.getATs())
                        .map(AT::new)
                        .toArray(AT[]::new));
    }

    @Override
    public Single<AmzID[]> getAccountBlockIDs(AmzAddress accountId) {
        return assign(blockchainService.getAccountBlockIDs(accountId.getID(), null, null, null))
                .map(response -> Arrays.stream(response.getBlockIds())
                        .map(AmzID::fromLong)
                        .toArray(AmzID[]::new));
    }

    @Override
    public Single<Block[]> getAccountBlocks(AmzAddress accountId) {
        return assign(blockchainService.getAccountBlocks(accountId.getID(), null, null, null, null))
                .map(response -> Arrays.stream(response.getBlocks())
                        .map(Block::new)
                        .toArray(Block[]::new));
    }

    @Override
    public Single<AmzID[]> getAccountTransactionIDs(AmzAddress accountId) {
        return assign(blockchainService.getAccountTransactionIDs(accountId.getID(), null, null, null, null, null, null))
                .map(response -> Arrays.stream(response.getTransactionIds())
                        .map(AmzID::fromLong)
                        .toArray(AmzID[]::new));
    }

    @Override
    public Single<Transaction[]> getAccountTransactions(AmzAddress accountId) {
        return assign(blockchainService.getAccountTransactions(accountId.getID(), null, null, null, null, null, null))
                .map(response -> Arrays.stream(response.getTransactions())
                        .map(Transaction::new)
                        .toArray(Transaction[]::new));
    }

    @Override
    public Single<AmzAddress[]> getAccountsWithRewardRecipient(AmzAddress accountId) {
        return assign(blockchainService.getAccountsWithRewardRecipient(accountId.getID()))
                .map(response -> Arrays.stream(response.getAccounts())
                        .map(AmzAddress::fromEither)
                        .toArray(AmzAddress[]::new));
    }

    @Override
    public Single<AT> getAt(AmzAddress atId) {
        return assign(blockchainService.getAt(atId.getID()))
                .map(AT::new);
    }

    @Override
    public Single<AmzAddress[]> getAtIds() {
        return assign(blockchainService.getAtIds())
                .map(response -> Arrays.stream(response.getAtIds())
                        .map(AmzAddress::fromId)
                        .toArray(AmzAddress[]::new));
    }

    @Override
    public Single<Transaction> getTransaction(AmzID transactionId) {
        return assign(blockchainService.getTransaction(transactionId.getID(), null))
                .map(Transaction::new);
    }

    @Override
    public Single<Transaction> getTransaction(byte[] fullHash) {
        return assign(blockchainService.getTransaction(null, Hex.toHexString(fullHash)))
                .map(Transaction::new);
    }

    @Override
    public Single<byte[]> getTransactionBytes(AmzID transactionId) {
        return assign(blockchainService.getTransactionBytes(transactionId.getID()))
                .map(response -> Hex.decode(response.getTransactionBytes()));
    }

    @Override
    public Single<byte[]> generateTransaction(AmzAddress recipient, byte[] senderPublicKey, AmzValue amount, AmzValue fee, int deadline) {
        return assign(blockchainService.sendMoney(recipient.getID(), null, amount.toPlanck().toString(), null, Hex.toHexString(senderPublicKey), fee.toPlanck().toString(), deadline, null, false, null, null, null, null, null, null, null, null, null, null))
                .map(response -> Hex.decode(response.getUnsignedTransactionBytes()));
    }

    @Override
    public Single<byte[]> generateTransactionWithMessage(AmzAddress recipient, byte[] senderPublicKey, AmzValue amount, AmzValue fee, int deadline, String message) {
        return assign(blockchainService.sendMoney(recipient.getID(), null, amount.toPlanck().toString(), null, Hex.toHexString(senderPublicKey), fee.toPlanck().toString(), deadline, null, false, message, true, null, null, null, null, null, null, null, null))
                .map(response -> Hex.decode(response.getUnsignedTransactionBytes()));
    }

    @Override
    public Single<byte[]> generateTransactionWithMessage(AmzAddress recipient, byte[] senderPublicKey, AmzValue amount, AmzValue fee, int deadline, byte[] message) {
        return assign(blockchainService.sendMoney(recipient.getID(), null, amount.toPlanck().toString(), null, Hex.toHexString(senderPublicKey), fee.toPlanck().toString(), deadline, null, false, Hex.toHexString(message), false, null, null, null, null, null, null, null, null))
                .map(response -> Hex.decode(response.getUnsignedTransactionBytes()));
    }

    @Override
    public Single<byte[]> generateTransactionWithEncryptedMessage(AmzAddress recipient, byte[] senderPublicKey, AmzValue amount, AmzValue fee, int deadline, AmzEncryptedMessage message) {
        return assign(blockchainService.sendMoney(recipient.getID(), null, amount.toPlanck().toString(), null, Hex.toHexString(senderPublicKey), fee.toPlanck().toString(), deadline, null, false, null, null, null, message.isText(), Hex.toHexString(message.getData()), Hex.toHexString(message.getNonce()) ,null, null, null, null))
                .map(response -> Hex.decode(response.getUnsignedTransactionBytes()));
    }

    @Override
    public Single<byte[]> generateTransactionWithEncryptedMessageToSelf(AmzAddress recipient, byte[] senderPublicKey, AmzValue amount, AmzValue fee, int deadline, AmzEncryptedMessage message) {
        return assign(blockchainService.sendMoney(recipient.getID(), null, amount.toPlanck().toString(), null, Hex.toHexString(senderPublicKey), fee.toPlanck().toString(), deadline, null, false, null, null, null, null, null, null, null, message.isText(), Hex.toHexString(message.getData()), Hex.toHexString(message.getNonce())))
                .map(response -> Hex.decode(response.getUnsignedTransactionBytes()));
    }

    @Override
    public Single<FeeSuggestion> suggestFee() {
        return assign(blockchainService.suggestFee())
                .map(FeeSuggestion::new);
    }

    @Override
    public Observable<MiningInfo> getMiningInfo() {
        AtomicReference<MiningInfoResponse> miningInfo = new AtomicReference<>();
        return assign(Observable.interval(0, 1, TimeUnit.SECONDS)
                .flatMapSingle(l -> blockchainService.getMiningInfo()
                        .map(this::checkBrsResponse))
                .filter(newMiningInfo -> {
                    synchronized (miningInfo) {
                        if (miningInfo.get() == null || !Objects.equals(miningInfo.get().getGenerationSignature(), newMiningInfo.getGenerationSignature()) || !Objects.equals(miningInfo.get().getHeight(), newMiningInfo.getHeight())) {
                            miningInfo.set(newMiningInfo);
                            return true;
                        } else {
                            return false;
                        }
                    }
                })
                .map(MiningInfo::new));
    }

    @Override
    public Single<TransactionBroadcast> broadcastTransaction(byte[] transactionBytes) {
        return assign(blockchainService.broadcastTransaction(Hex.toHexString(transactionBytes)))
                .map(TransactionBroadcast::new);
    }

    @Override
    public Single<AmzAddress> getRewardRecipient(AmzAddress account) {
        return assign(blockchainService.getRewardRecipient(account.getID()))
                .map(response -> AmzAddress.fromEither(response.getRewardRecipient()));
    }

    @Override
    public Single<Long> submitNonce(String passphrase, String nonce, AmzID accountId) {
        return assign(blockchainService.submitNonce(passphrase, nonce, accountId == null ? null : accountId.getID(), ""))
                .map(submitNonceResponse -> {
                    if (!Objects.equals(submitNonceResponse.getResult(), "success")) {
                        throw new AmzApiException("Failed to submit nonce: " + submitNonceResponse.getResult());
                    }
                    return submitNonceResponse;
                })
                .map(SubmitNonceResponse::getDeadline);
    }

    @Override
    public Single<byte[]> generateMultiOutTransaction(byte[] senderPublicKey, AmzValue fee, int deadline, Map<AmzAddress, AmzValue> recipients) throws IllegalArgumentException {
        return Single.fromCallable(() -> {
            StringBuilder recipientsString = new StringBuilder();
            if (recipients.size() > 64 || recipients.size() < 2) {
                throw new IllegalArgumentException("Must have 2-64 recipients, had " + recipients.size());
            }
            for (Map.Entry<AmzAddress, AmzValue> recipient : recipients.entrySet()) {
                recipientsString.append(recipient.getKey().getID()).append(":").append(recipient.getValue().toPlanck()).append(";");
            }
            recipientsString.setLength(recipientsString.length() - 1);
            return recipientsString;
        }).flatMap(recipientsString -> assign(blockchainService.sendMoneyMulti(null, Hex.toHexString(senderPublicKey), fee.toPlanck().toString(), String.valueOf(deadline), null, false, recipientsString.toString()))
                .map(response -> Hex.decode(response.getUnsignedTransactionBytes())));
    }

    @Override
    public Single<byte[]> generateMultiOutSameTransaction(byte[] senderPublicKey, AmzValue amount, AmzValue fee, int deadline, Set<AmzAddress> recipients) throws IllegalArgumentException {
        return Single.fromCallable(() -> {
            StringBuilder recipientsString = new StringBuilder();
            if (recipients.size() > 128 || recipients.size() < 2) {
                throw new IllegalArgumentException("Must have 2-128 recipients, had " + recipients.size());
            }
            for (AmzAddress recipient : recipients) {
                recipientsString.append(recipient.getID()).append(";");
            }
            recipientsString.setLength(recipientsString.length() - 1);
            return recipientsString;
        }).flatMap(recipientsString -> assign(blockchainService.sendMoneyMultiSame(null, Hex.toHexString(senderPublicKey), fee.toPlanck().toString(), String.valueOf(deadline), null, false, recipientsString.toString(), amount.toPlanck().toString()))
                .map(response -> Hex.decode(response.getUnsignedTransactionBytes())));
    }

    @Override
    public Single<byte[]> generateCreateATTransaction(byte[] senderPublicKey, AmzValue fee, int deadline, String name, String description, byte[] creationBytes) {
        return assign(blockchainService.createATProgram(Hex.toHexString(senderPublicKey), fee.toPlanck().toString(), deadline, false, name, description, Hex.toHexString(creationBytes), null, null, 0, 0, 0, null))
                .map(response -> {
                    if (response.getError() != null) throw new IllegalArgumentException(response.getError());
                    return response;
                })
                .map(response -> Hex.decode(response.getUnsignedTransactionBytes()));
    }

    private interface BlockchainService {
        @GET("amz?requestType=getBlock")
        Single<BlockResponse> getBlock(@Query("block") String blockId, @Query("height") String blockHeight, @Query("timestamp") String timestamp, @Query("includeTransactions") String[] transactions); // TODO Array of transactions

        @GET("amz?requestType=getBlockId")
        Single<BlockIDResponse> getBlockID(@Query("height") String blockHeight);

        @GET("amz?requestType=getBlocks")
        Single<BlocksResponse> getBlocks(@Query("firstIndex") String firstIndex, @Query("lastIndex") String lastIndex, @Query("includeTransactions") String[] transactions);

        @GET("amz?requestType=getConstants")
        Single<ConstantsResponse> getConstants();

        @GET("amz?requestType=getAccount")
        Single<AccountResponse> getAccount(@Query("account") String accountId);

        @GET("amz?requestType=getAccountATs")
        Single<AccountATsResponse> getAccountATs(@Query("account") String accountId);

        @GET("amz?requestType=getAccountBlockIds")
        Single<AccountBlockIDsResponse> getAccountBlockIDs(@Query("account") String accountId, @Query("timestamp") String timestamp, @Query("firstIndex") String firstIndex, @Query("lastIndex") String lastIndex);

        @GET("amz?requestType=getAccountBlocks")
        Single<AccountBlocksResponse> getAccountBlocks(@Query("account") String accountId, @Query("timestamp") String timestamp, @Query("firstIndex") String firstIndex, @Query("lastIndex") String lastIndex, @Query("includeTransactions") String[] includedTransactions);

        @GET("amz?requestType=getAccountTransactionIds")
        Single<AccountTransactionIDsResponse> getAccountTransactionIDs(@Query("account") String accountId, @Query("timestamp") String timestamp, @Query("type") String type, @Query("subtype") String subtype, @Query("firstIndex") String firstIndex, @Query("lastIndex") String lastIndex, @Query("numberOfConfirmations") String numberOfConfirmations);

        @GET("amz?requestType=getAccountTransactions")
        Single<AccountTransactionsResponse> getAccountTransactions(@Query("account") String accountId, @Query("timestamp") String timestamp, @Query("type") String type, @Query("subtype") String subtype, @Query("firstIndex") String firstIndex, @Query("lastIndex") String lastIndex, @Query("numberOfConfirmations") String numberOfConfirmations);

        @GET("amz?requestType=getAccountsWithRewardRecipient")
        Single<AccountsWithRewardRecipientResponse> getAccountsWithRewardRecipient(@Query("account") String accountId);

        @GET("amz?requestType=getAT")
        Single<ATResponse> getAt(@Query("at") String atId);

        @GET("amz?requestType=getATIds")
        Single<AtIDsResponse> getAtIds();

        @GET("amz?requestType=getTransaction")
        Single<TransactionResponse> getTransaction(@Query("transaction") String transaction, @Query("fullHash") String fullHash);

        @GET("amz?requestType=getTransactionBytes")
        Single<TransactionBytesResponse> getTransactionBytes(@Query("transaction") String transaction);

        @POST("amz?requestType=sendMoney")
        Single<GenerateTransactionResponse> sendMoney(@Query("recipient") String recipient, @Query("recipientPublicKey") String recipientPublicKey, @Query("amountNQT") String amount, @Query("secretPhrase") String secretPhrase, @Query("publicKey") String publicKey, @Query("feeNQT") String fee, @Query("deadline") int deadline, @Query("referencedTransactionFullHash") String referencedTransactionFullHash, @Query("broadcast") boolean broadcast, @Query("message") String message, @Query("messageIsText") Boolean messageIsText, @Query("messageToEncrypt") String messageToEncrypt, @Query("messageToEncryptIsText") Boolean messageToEncryptIsText, @Query("encryptedMessageData") String encryptedMessageData, @Query("encryptedMessageNonce") String encryptedMessageNonce, @Query("messageToEncryptToSelf") String messageToEncryptToSelf, @Query("messageToEncryptToSelfIsText") Boolean messageToEncryptToSelfIsText, @Query("encryptedToSelfMessageData") String encryptedToSelfMessageData, @Query("encryptedToSelfMessageNonce") String encryptedToSelfMessageNonce);

        @GET("amz?requestType=suggestFee")
        Single<SuggestFeeResponse> suggestFee();

        @GET("amz?requestType=getMiningInfo")
        Single<MiningInfoResponse> getMiningInfo();

        @POST("amz?requestType=broadcastTransaction")
        Single<BroadcastTransactionResponse> broadcastTransaction(@Query("transactionBytes") String transactionBytes);

        @GET("amz?requestType=getRewardRecipient")
        Single<RewardRecipientResponse> getRewardRecipient(@Query("account") String account);

        @POST("amz?requestType=submitNonce")
        Single<SubmitNonceResponse> submitNonce(@Query("secretPhrase") String passphrase, @Query("nonce") String nonce, @Query("accountId") String accountId, @Query("blockheight") String blockheight);

        @POST("amz?requestType=sendMoneyMulti")
        Single<GenerateTransactionResponse> sendMoneyMulti(@Query("secretPhrase") String secretPhrase, @Query("publicKey") String publicKey, @Query("feeNQT") String feeNQT, @Query("deadline") String deadline, @Query("referencedTransactionFullHash") String referencedTransactionFullHash, @Query("broadcast") boolean broadcast, @Query("recipients") String recipients);

        @POST("amz?requestType=sendMoneyMultiSame")
        Single<GenerateTransactionResponse> sendMoneyMultiSame(@Query("secretPhrase") String secretPhrase, @Query("publicKey") String publicKey, @Query("feeNQT") String feeNQT, @Query("deadline") String deadline, @Query("referencedTransactionFullHash") String referencedTransactionFullHash, @Query("broadcast") boolean broadcast, @Query("recipients") String recipients, @Query("amountNQT") String amountNQT);

        @POST("amz?requestType=createATProgram")
        Single<CreateATResponse> createATProgram(@Query("publicKey") String publicKey, @Query("feeNQT") String fee, @Query("deadline") int deadline, @Query("broadcast") boolean broadcast, @Query("name") String name, @Query("description") String description, @Query("creationBytes") String creationBytes, @Query("code") String code, @Query("data") String data, @Query("dpages") int dpages, @Query("cspages") int cspages, @Query("uspages") int uspages, @Query("minActivationAmountNQT") String minActivationAmountNQT);
    }
}
