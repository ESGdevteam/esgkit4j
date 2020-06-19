package amz.kit.entity.response.http.attachment;

import amz.kit.entity.AmzAddress;
import amz.kit.entity.response.TransactionAttachment;
import amz.kit.entity.response.attachment.MultiOutSameAttachment;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public final class MultiOutSameAttachmentResponse extends TransactionAttachmentResponse {
    @SerializedName("version.MultiSameOutCreation")
    private final int version;
    private final String[] recipients;

    public MultiOutSameAttachmentResponse(int version, String[] recipients) {
        this.version = version;
        this.recipients = recipients;
    }

    public int getVersion() {
        return version;
    }

    public String[] getRecipients() {
        return recipients;
    }

    @Override
    public TransactionAttachment toAttachment() {
        return new MultiOutSameAttachment(version, Arrays.stream(recipients)
                .map(AmzAddress::fromId)
                .toArray(AmzAddress[]::new));
    }
}
