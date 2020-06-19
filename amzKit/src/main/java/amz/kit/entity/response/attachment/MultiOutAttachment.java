package amz.kit.entity.response.attachment;

import amz.kit.entity.AmzAddress;
import amz.kit.entity.AmzValue;
import amz.kit.entity.response.TransactionAttachment;
import amz.kit.service.impl.grpc.BrsApi;

import java.util.Map;
import java.util.stream.Collectors;

public class MultiOutAttachment extends TransactionAttachment {
    private final Map<AmzAddress, AmzValue> outputs;

    public MultiOutAttachment(int version, Map<AmzAddress, AmzValue> outputs) {
        super(version);
        this.outputs = outputs;
    }

    public MultiOutAttachment(BrsApi.MultiOutAttachment multiOutAttachment) {
        super(multiOutAttachment.getVersion());
        this.outputs = multiOutAttachment.getRecipientsList()
                .stream()
                .collect(Collectors.toMap(recipient -> AmzAddress.fromId(recipient.getRecipient()), recipient -> AmzValue.fromPlanck(recipient.getAmount())));
    }

    public Map<AmzAddress, AmzValue> getOutputs() {
        return outputs;
    }
}
