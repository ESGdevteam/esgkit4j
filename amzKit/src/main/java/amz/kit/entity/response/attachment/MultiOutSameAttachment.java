package amz.kit.entity.response.attachment;

import amz.kit.entity.AmzAddress;
import amz.kit.entity.response.TransactionAttachment;
import amz.kit.service.impl.grpc.BrsApi;

public class MultiOutSameAttachment extends TransactionAttachment {
    private AmzAddress[] recipients;

    public MultiOutSameAttachment(int version, AmzAddress[] recipients) {
        super(version);
        this.recipients = recipients;
    }

    public MultiOutSameAttachment(BrsApi.MultiOutSameAttachment multiOutSameAttachment) {
        super(multiOutSameAttachment.getVersion());
        this.recipients = multiOutSameAttachment.getRecipientsList()
                .stream()
                .map(AmzAddress::fromId)
                .toArray(AmzAddress[]::new);
    }

    public AmzAddress[] getRecipients() {
        return recipients;
    }
}
