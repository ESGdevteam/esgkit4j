package amz.kit.entity.response.http.attachment;

import amz.kit.entity.response.TransactionAttachment;

public abstract class TransactionAttachmentResponse {
    public abstract TransactionAttachment toAttachment();
}
