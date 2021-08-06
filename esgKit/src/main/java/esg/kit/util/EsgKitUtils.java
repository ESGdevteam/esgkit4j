package esg.kit.util;

import esg.kit.entity.*;
import esg.kit.entity.response.http.attachment.MultiOutAttachmentResponse;
import esg.kit.entity.response.http.attachment.TransactionAttachmentAndAppendagesResponse;
import com.google.gson.GsonBuilder;

@SuppressWarnings("WeakerAccess")
public final class EsgKitUtils {
    public static GsonBuilder buildGson(GsonBuilder builder) {
        return builder
                // Response entities
                .registerTypeAdapter(TransactionAttachmentAndAppendagesResponse.class, TransactionAttachmentAndAppendagesResponse.SERIALIZER)
                .registerTypeAdapter(TransactionAttachmentAndAppendagesResponse.class, TransactionAttachmentAndAppendagesResponse.DESERIALIZER)
                .registerTypeAdapter(MultiOutAttachmentResponse.MultiOutRecipient.class, MultiOutAttachmentResponse.MultiOutRecipient.SERIALIZER)
                .registerTypeAdapter(MultiOutAttachmentResponse.MultiOutRecipient.class, MultiOutAttachmentResponse.MultiOutRecipient.DESERIALIZER)
                ;
    }

    public static GsonBuilder buildGson() {
        return buildGson(new GsonBuilder());
    }
}
