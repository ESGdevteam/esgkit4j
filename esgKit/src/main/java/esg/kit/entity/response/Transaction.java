package esg.kit.entity.response;

import esg.kit.crypto.EsgCrypto;
import esg.kit.entity.EsgAddress;
import esg.kit.entity.EsgID;
import esg.kit.entity.EsgTimestamp;
import esg.kit.entity.EsgValue;
import esg.kit.entity.response.attachment.OrdinaryPaymentAttachment;
import esg.kit.entity.response.http.TransactionResponse;
import esg.kit.entity.response.http.attachment.TransactionAppendixResponse;
import esg.kit.service.impl.grpc.BrsApi;
import org.bouncycastle.util.encoders.Hex;

import java.util.Arrays;

public class Transaction {
    private final EsgAddress recipient;
    private final EsgAddress sender;
    private final EsgID blockId;
    private final EsgID ecBlockId;
    private final EsgID id;
    private final EsgTimestamp blockTimestamp;
    private final EsgTimestamp timestamp;
    private final EsgValue amount;
    private final EsgValue fee;
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

    public Transaction(EsgAddress recipient, EsgAddress sender, EsgID blockId, EsgID ecBlockId, EsgID id, EsgTimestamp blockTimestamp, EsgTimestamp timestamp, EsgValue amount, EsgValue fee, byte[] fullHash, byte[] referencedTransactionFullHash, byte[] senderPublicKey, byte[] signature, byte[] signatureHash, int blockHeight, int confirmations, int ecBlockHeight, int subtype, int type, int version, TransactionAttachment attachment, TransactionAppendix[] appendages, short deadline) {
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
        this.recipient = EsgAddress.fromEither(transactionResponse.getRecipient());
        this.sender = EsgAddress.fromEither(transactionResponse.getSender());
        this.blockId = EsgID.fromLong(transactionResponse.getBlock());
        this.ecBlockId = EsgID.fromLong(transactionResponse.getEcBlockId());
        this.id = EsgID.fromLong(transactionResponse.getTransaction());
        this.blockTimestamp = new EsgTimestamp(transactionResponse.getBlockTimestamp());
        this.timestamp = new EsgTimestamp(transactionResponse.getTimestamp());
        this.amount = EsgValue.fromPlanck(transactionResponse.getAmountNQT());
        this.fee = EsgValue.fromPlanck(transactionResponse.getFeeNQT());
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
        EsgCrypto esgCrypto = EsgCrypto.getInstance();
        BrsApi.BasicTransaction basicTransaction = transaction.getTransaction();
        this.recipient = EsgAddress.fromId(transaction.getId());
        this.sender = EsgAddress.fromId(basicTransaction.getSenderId());
        this.blockId = EsgID.fromLong(transaction.getBlock());
        this.ecBlockId = EsgID.fromLong(basicTransaction.getEcBlockId());
        this.id = EsgID.fromLong(transaction.getId());
        this.blockTimestamp = new EsgTimestamp(transaction.getBlockTimestamp());
        this.timestamp = new EsgTimestamp(basicTransaction.getTimestamp());
        this.amount = EsgValue.fromPlanck(basicTransaction.getAmount());
        this.fee = EsgValue.fromPlanck(basicTransaction.getFee());
        this.fullHash = transaction.getFullHash().toByteArray();
        this.referencedTransactionFullHash = basicTransaction.getReferencedTransactionFullHash().toByteArray();
        this.senderPublicKey = basicTransaction.getSenderPublicKey().toByteArray();
        this.signature = basicTransaction.getSignature().toByteArray();
        this.signatureHash = esgCrypto.getSha256().digest(basicTransaction.getSignature().toByteArray()); // TODO check this is correct
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

    public EsgAddress getRecipient() {
        return recipient;
    }

    public EsgAddress getSender() {
        return sender;
    }

    public EsgID getBlockId() {
        return blockId;
    }

    public EsgID getEcBlockId() {
        return ecBlockId;
    }

    public EsgID getId() {
        return id;
    }

    public EsgTimestamp getBlockTimestamp() {
        return blockTimestamp;
    }

    public EsgTimestamp getTimestamp() {
        return timestamp;
    }

    public EsgValue getAmount() {
        return amount;
    }

    public EsgValue getFee() {
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
