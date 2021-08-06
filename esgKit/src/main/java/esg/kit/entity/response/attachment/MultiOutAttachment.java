package esg.kit.entity.response.attachment;

import esg.kit.entity.EsgAddress;
import esg.kit.entity.EsgValue;
import esg.kit.entity.response.TransactionAttachment;
import esg.kit.service.impl.grpc.BrsApi;

import java.util.Map;
import java.util.stream.Collectors;

public class MultiOutAttachment extends TransactionAttachment {
    private final Map<EsgAddress, EsgValue> outputs;

    public MultiOutAttachment(int version, Map<EsgAddress, EsgValue> outputs) {
        super(version);
        this.outputs = outputs;
    }

    public MultiOutAttachment(BrsApi.MultiOutAttachment multiOutAttachment) {
        super(multiOutAttachment.getVersion());
        this.outputs = multiOutAttachment.getRecipientsList()
                .stream()
                .collect(Collectors.toMap(recipient -> EsgAddress.fromId(recipient.getRecipient()), recipient -> EsgValue.fromPlanck(recipient.getAmount())));
    }

    public Map<EsgAddress, EsgValue> getOutputs() {
        return outputs;
    }
}
