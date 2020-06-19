package amz.kit.entity.response.http.attachment;

import amz.kit.entity.response.TransactionAttachment;
import amz.kit.entity.response.attachment.OrdinaryPaymentAttachment;

@SuppressWarnings("WeakerAccess")
public final class OrdinaryPaymentAttachmentResponse extends TransactionAttachmentResponse {
    OrdinaryPaymentAttachmentResponse() {}

    @Override
    public TransactionAttachment toAttachment() {
        return new OrdinaryPaymentAttachment(1);
    }
}
