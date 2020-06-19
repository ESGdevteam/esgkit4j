package amz.kit.entity.response;

import amz.kit.crypto.AmzCrypto;
import amz.kit.entity.AmzAddress;
import amz.kit.entity.AmzID;
import amz.kit.entity.AmzTimestamp;
import amz.kit.entity.AmzValue;
import amz.kit.entity.response.attachment.OrdinaryPaymentAttachment;
import amz.kit.entity.response.http.TransactionResponse;
import amz.kit.entity.response.http.attachment.TransactionAppendixResponse;
import amz.kit.service.impl.grpc.BrsApi;
import org.bouncycastle.util.encoders.Hex;

import java.util.Arrays;

public class Transaction {
    private final AmzAddress recipient;
    private final AmzAddress sender;
    private final AmzID blockId;
    private final AmzID ecBlockId;
    private final AmzID id;
    private final AmzTimestamp blockTimestamp;
    private final AmzTimestamp timestamp;
    private final AmzValue amount;
    private final AmzValue fee;
    private final byte[] fullHash;
    private final byte[] referencedTransactionFullHash;
    private final byte[] senderPublicKey;
    private final byte[] signature;
    private final byte[] signatureHash;
    private final int blockHeight;
    private final int confirmations;
    private final int ecBlockHeight;
    private final int subtype;
    private final int type;
    private final int version;
    private final TransactionAttachment attachment;
    private final TransactionAppendix[] appendages;
    private final short deadline;

    public Transaction(AmzAddress recipient, AmzAddress sender, AmzID blockId, AmzID ecBlockId, AmzID id, AmzTimestamp blockTimestamp, AmzTimestamp timestamp, AmzValue amount, AmzValue fee, byte[] fullHash, byte[] referencedTransactionFullHash, byte[] senderPublicKey, byte[] signature, byte[] signatureHash, int blockHeight, int confirmations, int ecBlockHeight, int subtype, int type, int version, TransactionAttachment attachment, TransactionAppendix[] appendages, short deadline) {
        this.recipient = recipient;
        this.sender = sender;
        this.blockId = blockId;
        this.ecBlockId = ecBlockId;
        this.id = id;
        this.blockTimestamp = blockTimestamp;
        this.timestamp = timestamp;
        this.amount = amount;
        this.fee = fee;
        this.fullHash = fullHash;
        this.referencedTransactionFullHash = referencedTransactionFullHash;
        this.senderPublicKey = senderPublicKey;
        this.signature = signature;
        this.signatureHash = signatureHash;
        this.blockHeight = blockHeight;
        this.confirmations = confirmations;
        this.ecBlockHeight = ecBlockHeight;
        this.subtype = subtype;
        this.type = type;
        this.version = version;
        this.attachment = attachment;
        this.appendages = appendages;
        this.deadline = deadline;
    }

    public Transaction(TransactionResponse transactionResponse) {
        this.recipient = AmzAddress.fromEither(transactionResponse.getRecipient());
        this.sender = AmzAddress.fromEither(transactionResponse.getSender());
        this.blockId = AmzID.fromLong(transactionResponse.getBlock());
        this.ecBlockId = AmzID.fromLong(transactionResponse.getEcBlockId());
        this.id = AmzID.fromLong(transactionResponse.getTransaction());
        this.blockTimestamp = new AmzTimestamp(transactionResponse.getBlockTimestamp());
        this.timestamp = new AmzTimestamp(transactionResponse.getTimestamp());
        this.amount = AmzValue.fromPlanck(transactionResponse.getAmountNQT());
        this.fee = AmzValue.fromPlanck(transactionResponse.getFeeNQT());
        this.fullHash = Hex.decode(transactionResponse.getFullHash());
        this.referencedTransactionFullHash = transactionResponse.getReferencedTransactionFullHash() == null ? null : Hex.decode(transactionResponse.getReferencedTransactionFullHash());
        this.senderPublicKey = Hex.decode(transactionResponse.getSenderPublicKey());
        this.signature = Hex.decode(transactionResponse.getSignature());
        this.signatureHash = Hex.decode(transactionResponse.getSignatureHash());
        this.blockHeight = transactionResponse.getHeight();
        this.confirmations = transactionResponse.getConfirmations();
        this.ecBlockHeight = transactionResponse.getEcBlockHeight();
        this.subtype = transactionResponse.getSubtype();
        this.type = transactionResponse.getType();
        this.version = transactionResponse.getVersion();
        this.attachment = transactionResponse.getAttachment() == null ? new OrdinaryPaymentAttachment(transactionResponse.getVersion()) : transactionResponse.getAttachment().getAttachment().toAttachment();
        this.appendages = transactionResponse.getAttachment() == null ? new TransactionAppendix[0] : Arrays.stream(transactionResponse.getAttachment().getAppendages())
                .map(TransactionAppendixResponse::toAppendix)
                .toArray(TransactionAppendix[]::new);
        this.deadline = transactionResponse.getDeadline();
    }

    public Transaction(BrsApi.Transaction transaction) {
        AmzCrypto amzCrypto = AmzCrypto.getInstance();
        BrsApi.BasicTransaction basicTransaction = transaction.getTransaction();
        this.recipient = AmzAddress.fromId(transaction.getId());
        this.sender = AmzAddress.fromId(basicTransaction.getSenderId());
        this.blockId = AmzID.fromLong(transaction.getBlock());
        this.ecBlockId = AmzID.fromLong(basicTransaction.getEcBlockId());
        this.id = AmzID.fromLong(transaction.getId());
        this.blockTimestamp = new AmzTimestamp(transaction.getBlockTimestamp());
        this.timestamp = new AmzTimestamp(basicTransaction.getTimestamp());
        this.amount = AmzValue.fromPlanck(basicTransaction.getAmount());
        this.fee = AmzValue.fromPlanck(basicTransaction.getFee());
        this.fullHash = transaction.getFullHash().toByteArray();
        this.referencedTransactionFullHash = basicTransaction.getReferencedTransactionFullHash().toByteArray();
        this.senderPublicKey = basicTransaction.getSenderPublicKey().toByteArray();
        this.signature = basicTransaction.getSignature().toByteArray();
        this.signatureHash = amzCrypto.getSha256().digest(basicTransaction.getSignature().toByteArray()); // TODO check this is correct
        this.blockHeight = transaction.getBlockHeight();
        this.confirmations = transaction.getConfirmations();
        this.ecBlockHeight = basicTransaction.getEcBlockHeight();
        this.subtype = basicTransaction.getSubtype();
        this.type = basicTransaction.getType();
        this.version = basicTransaction.getVersion();
        this.attachment = TransactionAttachment.fromProtobuf(basicTransaction.getAttachment(), basicTransaction.getVersion());
        this.appendages = basicTransaction.getAppendagesList()
                .stream()
                .map(TransactionAppendix::fromProtobuf)
                .toArray(TransactionAppendix[]::new);
        this.deadline = (short) basicTransaction.getDeadline();
    }

    public AmzAddress getRecipient() {
        return recipient;
    }

    public AmzAddress getSender() {
        return sender;
    }

    public AmzID getBlockId() {
        return blockId;
    }

    public AmzID getEcBlockId() {
        return ecBlockId;
    }

    public AmzID getId() {
        return id;
    }

    public AmzTimestamp getBlockTimestamp() {
        return blockTimestamp;
    }

    public AmzTimestamp getTimestamp() {
        return timestamp;
    }

    public AmzValue getAmount() {
        return amount;
    }

    public AmzValue getFee() {
        return fee;
    }

    public byte[] getFullHash() {
        return fullHash;
    }

    public byte[] getReferencedTransactionFullHash() {
        return referencedTransactionFullHash;
    }

    public byte[] getSenderPublicKey() {
        return senderPublicKey;
    }

    public byte[] getSignature() {
        return signature;
    }

    public byte[] getSignatureHash() {
        return signatureHash;
    }

    public int getBlockHeight() {
        return blockHeight;
    }

    public int getConfirmations() {
        return confirmations;
    }

    public int getEcBlockHeight() {
        return ecBlockHeight;
    }

    public int getSubtype() {
        return subtype;
    }

    public int getType() {
        return type;
    }

    public int getVersion() {
        return version;
    }

    public TransactionAttachment getAttachment() {
        return attachment;
    }

    public TransactionAppendix[] getAppendages() {
        return appendages;
    }

    public short getDeadline() {
        return deadline;
    }
}
