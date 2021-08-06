package esg.kit.entity.response;

import esg.kit.crypto.EsgCrypto;
import esg.kit.entity.EsgID;
import esg.kit.entity.response.http.BroadcastTransactionResponse;
import esg.kit.service.impl.grpc.BrsApi;
import org.bouncycastle.util.encoders.Hex;

public class TransactionBroadcast {
    private final byte[] fullHash;
    private final EsgID transactionId;
    private final int numberPeersSentTo;

    public TransactionBroadcast(byte[] fullHash, EsgID transactionId, int numberPeersSentTo) {
        this.fullHash = fullHash;
        this.transactionId = transactionId;
        this.numberPeersSentTo = numberPeersSentTo;
    }

    public TransactionBroadcast(BroadcastTransactionResponse response) {
        this.fullHash = Hex.decode(response.getFullHash());
        this.transactionId = EsgID.fromLong(response.getTransactionID());
        this.numberPeersSentTo = response.getNumberPeersSentTo();
    }

    public TransactionBroadcast(BrsApi.TransactionBroadcastResult transactionBroadcastResult, byte[] transactionBytes) {
        EsgCrypto esgCrypto = EsgCrypto.getInstance();
        this.fullHash = esgCrypto.getSha256().digest(transactionBytes);
        this.transactionId = esgCrypto.hashToId(this.fullHash);
        this.numberPeersSentTo = transactionBroadcastResult.getNumberOfPeersSentTo();
    }

    public byte[] getFullHash() {
        return fullHash;
    }

    public EsgID getTransactionId() {
        return transactionId;
    }

    public int getNumberPeersSentTo() {
        return numberPeersSentTo;
    }
}
