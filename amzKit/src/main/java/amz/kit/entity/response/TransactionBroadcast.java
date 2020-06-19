package amz.kit.entity.response;

import amz.kit.crypto.AmzCrypto;
import amz.kit.entity.AmzID;
import amz.kit.entity.response.http.BroadcastTransactionResponse;
import amz.kit.service.impl.grpc.BrsApi;
import org.bouncycastle.util.encoders.Hex;

public class TransactionBroadcast {
    private final byte[] fullHash;
    private final AmzID transactionId;
    private final int numberPeersSentTo;

    public TransactionBroadcast(byte[] fullHash, AmzID transactionId, int numberPeersSentTo) {
        this.fullHash = fullHash;
        this.transactionId = transactionId;
        this.numberPeersSentTo = numberPeersSentTo;
    }

    public TransactionBroadcast(BroadcastTransactionResponse response) {
        this.fullHash = Hex.decode(response.getFullHash());
        this.transactionId = AmzID.fromLong(response.getTransactionID());
        this.numberPeersSentTo = response.getNumberPeersSentTo();
    }

    public TransactionBroadcast(BrsApi.TransactionBroadcastResult transactionBroadcastResult, byte[] transactionBytes) {
        AmzCrypto amzCrypto = AmzCrypto.getInstance();
        this.fullHash = amzCrypto.getSha256().digest(transactionBytes);
        this.transactionId = amzCrypto.hashToId(this.fullHash);
        this.numberPeersSentTo = transactionBroadcastResult.getNumberOfPeersSentTo();
    }

    public byte[] getFullHash() {
        return fullHash;
    }

    public AmzID getTransactionId() {
        return transactionId;
    }

    public int getNumberPeersSentTo() {
        return numberPeersSentTo;
    }
}
