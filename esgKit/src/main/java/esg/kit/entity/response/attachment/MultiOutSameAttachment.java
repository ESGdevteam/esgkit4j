package esg.kit.entity.response.attachment;

import esg.kit.entity.EsgAddress;
import esg.kit.entity.response.TransactionAttachment;
import esg.kit.service.impl.grpc.BrsApi;

public class MultiOutSameAttachment extends TransactionAttachment {
    private EsgAddress[] recipients;

    public MultiOutSameAttachment(int version, EsgAddress[] recipients) {
        super(version);
        this.recipients = recipients;
    }

    public MultiOutSameAttachment(BrsApi.MultiOutSameAttachment multiOutSameAttachment) {
        super(multiOutSameAttachment.getVersion());
        this.recipients = multiOutSameAttachment.getRecipientsList()
                .stream()
                .map(EsgAddress::fromId)
                .toArray(EsgAddress[]::new);
    }

    public EsgAddress[] getRecipients() {
        return recipients;
    }
}
