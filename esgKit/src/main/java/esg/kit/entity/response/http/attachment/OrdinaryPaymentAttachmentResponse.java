package esg.kit.entity.response.http.attachment;

import esg.kit.entity.response.TransactionAttachment;
import esg.kit.entity.response.attachment.OrdinaryPaymentAttachment;

@SuppressWarnings("WeakerAccess")
public final class OrdinaryPaymentAttachmentResponse extends TransactionAttachmentResponse {
    OrdinaryPaymentAttachmentResponse() {}

    @Override
    public TransactionAttachment toAttachment() {
        return new OrdinaryPaymentAttachment(1);
    }
}
