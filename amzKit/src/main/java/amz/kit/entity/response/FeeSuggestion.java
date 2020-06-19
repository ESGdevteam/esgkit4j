package amz.kit.entity.response;

import amz.kit.entity.AmzValue;
import amz.kit.entity.response.http.SuggestFeeResponse;
import amz.kit.service.impl.grpc.BrsApi;

public class FeeSuggestion {
    private final AmzValue cheapFee;
    private final AmzValue standardFee;
    private final AmzValue priorityFee;

    public FeeSuggestion(AmzValue cheapFee, AmzValue standardFee, AmzValue priorityFee) {
        this.cheapFee = cheapFee;
        this.standardFee = standardFee;
        this.priorityFee = priorityFee;
    }

    public FeeSuggestion(SuggestFeeResponse suggestFeeResponse) {
        this.cheapFee = AmzValue.fromPlanck(suggestFeeResponse.getCheap());
        this.standardFee = AmzValue.fromPlanck(suggestFeeResponse.getStandard());
        this.priorityFee = AmzValue.fromPlanck(suggestFeeResponse.getPriority());
    }

    public FeeSuggestion(BrsApi.FeeSuggestion feeSuggestion) {
        this.cheapFee = AmzValue.fromPlanck(feeSuggestion.getCheap());
        this.standardFee = AmzValue.fromPlanck(feeSuggestion.getStandard());
        this.priorityFee = AmzValue.fromPlanck(feeSuggestion.getPriority());
    }

    public AmzValue getCheapFee() {
        return cheapFee;
    }

    public AmzValue getStandardFee() {
        return standardFee;
    }

    public AmzValue getPriorityFee() {
        return priorityFee;
    }
}
