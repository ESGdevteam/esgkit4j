package esg.kit.entity.response.http;

/**
 * This class does not extend BRSResponse because it is only ever used as a subtype of responses.
 */
@SuppressWarnings("unused")
public final class TransactionSubtypeResponse {
    private final String description;
    private final int value;

    public TransactionSubtypeResponse(String description, int value) {
        this.description = description;
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public int getValue() {
        return value;
    }
}
