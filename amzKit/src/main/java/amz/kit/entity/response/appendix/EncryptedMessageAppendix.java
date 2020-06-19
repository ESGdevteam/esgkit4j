package amz.kit.entity.response.appendix;

import amz.kit.entity.AmzEncryptedMessage;
import amz.kit.entity.response.TransactionAppendix;
import amz.kit.service.impl.grpc.BrsApi;

public abstract class EncryptedMessageAppendix extends TransactionAppendix {
    private final AmzEncryptedMessage encryptedMessage;

    private EncryptedMessageAppendix(int version, AmzEncryptedMessage encryptedMessage) {
        super(version);
        this.encryptedMessage = encryptedMessage;
    }

    private static AmzEncryptedMessage encryptedMessageFromProtobuf(BrsApi.EncryptedData encryptedData, boolean isText) {
        return new AmzEncryptedMessage(encryptedData.getData().toByteArray(), encryptedData.getNonce().toByteArray(), isText);
    }

    public static EncryptedMessageAppendix fromProtobuf(BrsApi.EncryptedMessageAppendix encryptedMessageAppendix) {
        switch(encryptedMessageAppendix.getType()) {
            case TO_RECIPIENT:
                return new ToRecipient(encryptedMessageAppendix.getVersion(), encryptedMessageFromProtobuf(encryptedMessageAppendix.getEncryptedData(), encryptedMessageAppendix.getIsText()));
            case TO_SELF:
                return new ToRecipient(encryptedMessageAppendix.getVersion(), encryptedMessageFromProtobuf(encryptedMessageAppendix.getEncryptedData(), encryptedMessageAppendix.getIsText()));
            default:
                throw new IllegalArgumentException("Invalid type");
        }
    }

    public AmzEncryptedMessage getEncryptedMessage() {
        return encryptedMessage;
    }

    public static class ToRecipient extends EncryptedMessageAppendix {
        public ToRecipient(int version, AmzEncryptedMessage encryptedMessage) {
            super(version, encryptedMessage);
        }
    }

    public static class ToSelf extends EncryptedMessageAppendix {
        public ToSelf(int version, AmzEncryptedMessage encryptedMessage) {
            super(version, encryptedMessage);
        }
    }
}
