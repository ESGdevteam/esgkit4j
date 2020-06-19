package amz.kit.entity.response.attachment;

import amz.kit.entity.response.TransactionAttachment;
import amz.kit.service.impl.grpc.BrsApi;

// TODO this is currently the default for unsupported types
public class OrdinaryPaymentAttachment extends TransactionAttachment {
    public OrdinaryPaymentAttachment(int version) {
        super(version);
    }
}
