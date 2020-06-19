package amz.kit.entity;

import amz.kit.crypto.AmzCrypto;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public final class AmzAddress {

    /**
     * Stored without "AMZ-" prefix.
     */
    private final String address;
    private final AmzID numericID;

    private AmzAddress(AmzID amzID) {
        this.numericID = amzID;
        this.address = AmzCrypto.getInstance().rsEncode(numericID);
    }

    /**
     * @param amzID The numeric id that represents this Amz Address
     * @return A AmzAddress object that represents the specified numericId
     * @throws NumberFormatException if the numericId is not a valid number
     * @throws IllegalArgumentException if the numericId is outside the range of accepted numbers (less than 0 or greater than / equal to 2^64)
     */
    public static AmzAddress fromId(AmzID amzID) {
        return new AmzAddress(amzID);
    }

    /**
     * @param signedLongId The numeric id that represents this Amz Address, as a signed long
     * @return A AmzAddress object that represents the specified numericId
     * @throws NumberFormatException if the numericId is not a valid number
     * @throws IllegalArgumentException if the numericId is outside the range of accepted numbers (less than 0 or greater than / equal to 2^64)
     */
    public static AmzAddress fromId(long signedLongId) {
        return new AmzAddress(AmzID.fromLong(signedLongId));
    }

    /**
     * @param unsignedLongId The numeric id that represents this Amz Address
     * @return A AmzAddress object that represents the specified numericId
     * @throws NumberFormatException if the numericId is not a valid number
     * @throws IllegalArgumentException if the numericId is outside the range of accepted numbers (less than 0 or greater than / equal to 2^64)
     */
    public static AmzAddress fromId(String unsignedLongId) {
        return new AmzAddress(AmzID.fromLong(unsignedLongId));
    }

    public static AmzAddress fromRs(String RS) throws IllegalArgumentException {
        if (RS.startsWith("AMZ-")) {
            RS = RS.substring(4);
        }
        return new AmzAddress(AmzCrypto.getInstance().rsDecode(RS));
    }

    /**
     * Try to parse an input as either a numeric ID or an RS address.
     *
     * @param input the numeric ID or RS address of the Amz address
     * @return a AmzAddress if one could be parsed from the input, null otherwise
     */
    public static AmzAddress fromEither(String input) {
        if (input == null) return null;
        try {
            return AmzAddress.fromId(AmzID.fromLong(input));
        } catch (IllegalArgumentException e1) {
            try {
                return AmzAddress.fromRs(input);
            } catch (IllegalArgumentException e2) {
                return null;
            }
        }
    }

    /**
     * @return The AmzID of this address
     */
    public AmzID getAmzID() {
        return numericID;
    }

    /**
     * @return The unsigned long numeric ID this AmzAddress points to
     */
    public String getID() {
        return numericID.getID();
    }

    /**
     * @return The signed long numeric ID this AmzAddress points to
     */
    public long getSignedLongId() {
        return numericID.getSignedLongId();
    }

    /**
     * @return The ReedSolomon encoded address, without the "AMZ-" prefix
     */
    public String getRawAddress() {
        return address;
    }

    /**
     * @return The ReedSolomon encoded address, with the "AMZ-" prefix
     */
    public String getFullAddress() {
        if (address == null || address.length() == 0) {
            return "";
        } else {
            return "AMZ-" + address;
        }
    }

    @Override
    public String toString() {
        return getFullAddress();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AmzAddress && Objects.equals(numericID, ((AmzAddress) obj).numericID);
    }

    @Override
    public int hashCode() {
        return numericID.hashCode();
    }
}
