package esg.kit.entity.response;

import esg.kit.entity.EsgValue;
import esg.kit.entity.response.http.SuggestFeeResponse;
import esg.kit.service.impl.grpc.BrsApi;

public class FeeSuggestion {
    private final EsgValue cheapFee;
    private final EsgValue standardFee;
    private final EsgValue priorityFee;

    public FeeSuggestion(EsgValue cheapFee, EsgValue standardFee, EsgValue priorityFee) {
        this.cheapFee = cheapFee;
        this.standardFee = standardFee;
        this.priorityFee = priorityFee;
    }

    public FeeSuggestion(SuggestFeeResponse suggestFeeResponse) {
        this.cheapFee = EsgValue.fromPlanck(suggestFeeResponse.getCheap());
        this.standardFee = EsgValue.fromPlanck(suggestFeeResponse.getStandard());
        this.priorityFee = EsgValue.fromPlanck(suggestFeeResponse.getPriority());
    }

    public FeeSuggestion(BrsApi.FeeSuggestion feeSuggestion) {
        this.cheapFee = EsgValue.fromPlanck(feeSuggestion.getCheap());
        this.standardFee = EsgValue.fromPlanck(feeSuggestion.getStandard());
        this.priorityFee = EsgValue.fromPlanck(feeSuggestion.getPriority());
    }

    public EsgValue getCheapFee() {
        return cheapFee;
    }

    public EsgValue getStandardFee() {
        return standardFee;
    }

    public EsgValue getPriorityFee() {
        return priorityFee;
    }
}
