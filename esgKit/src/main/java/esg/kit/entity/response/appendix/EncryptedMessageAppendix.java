package esg.kit.entity.response.appendix;

import esg.kit.entity.EsgEncryptedMessage;
import esg.kit.entity.response.TransactionAppendix;
import esg.kit.service.impl.grpc.BrsApi;

public abstract class EncryptedMessageAppendix extends TransactionAppendix {
    private final EsgEncryptedMessage encryptedMessage;

    private EncryptedMessageAppendix(int version, EsgEncryptedMessage encryptedMessage) {
        super(version);
        this.encryptedMessage = encryptedMessage;
    }

    private static EsgEncryptedMessage encryptedMessageFromProtobuf(BrsApi.EncryptedData encryptedData, boolean isText) {
        return new EsgEncryptedMessage(encryptedData.getData().toByteArray(), encryptedData.getNonce().toByteArray(), isText);
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

    public EsgEncryptedMessage getEncryptedMessage() {
        return encryptedMessage;
    }

    public static class ToRecipient extends EncryptedMessageAppendix {
        public ToRecipient(int version, EsgEncryptedMessage encryptedMessage) {
            super(version, encryptedMessage);
        }
    }

    public static class ToSelf extends EncryptedMessageAppendix {
        public ToSelf(int version, EsgEncryptedMessage encryptedMessage) {
            super(version, encryptedMessage);
        }
    }
}
