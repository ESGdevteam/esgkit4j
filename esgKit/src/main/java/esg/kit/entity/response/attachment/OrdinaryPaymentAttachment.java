package esg.kit.entity.response.attachment;

import esg.kit.entity.response.TransactionAttachment;
import esg.kit.service.impl.grpc.BrsApi;

// TODO this is currently the default for unsupported types
public class OrdinaryPaymentAttachment extends TransactionAttachment {
    public OrdinaryPaymentAttachment(int version) {
        super(version);
    }
}
