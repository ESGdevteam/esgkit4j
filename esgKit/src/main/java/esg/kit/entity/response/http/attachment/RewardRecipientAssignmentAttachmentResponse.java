package esg.kit.entity.response.http.attachment;

import esg.kit.entity.response.TransactionAttachment;
import esg.kit.entity.response.attachment.RewardRecipientAssignmentAttachment;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("WeakerAccess")
public final class RewardRecipientAssignmentAttachmentResponse extends TransactionAttachmentResponse {
    @SerializedName("version.RewardRecipientAssignment")
    private final int version;

    public RewardRecipientAssignmentAttachmentResponse(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public TransactionAttachment toAttachment() {
        return new RewardRecipientAssignmentAttachment(version);
    }
}
