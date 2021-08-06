package esg.kit.entity;

import esg.kit.crypto.EsgCrypto;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public final class EsgAddress {

    /**
     * Stored without "ESG-" prefix.
     */
    private final String address;
    private final EsgID numericID;

    private EsgAddress(EsgID esgID) {
        this.numericID = esgID;
        this.address = EsgCrypto.getInstance().rsEncode(numericID);
    }

    /**
     * @param esgID The numeric id that represents this Esg Address
     * @return A EsgAddress object that represents the specified numericId
     * @throws NumberFormatException if the numericId is not a valid number
     * @throws IllegalArgumentException if the numericId is outside the range of accepted numbers (less than 0 or greater than / equal to 2^64)
     */
    public static EsgAddress fromId(EsgID esgID) {
        return new EsgAddress(esgID);
    }

    /**
     * @param signedLongId The numeric id that represents this Esg Address, as a signed long
     * @return A EsgAddress object that represents the specified numericId
     * @throws NumberFormatException if the numericId is not a valid number
     * @throws IllegalArgumentException if the numericId is outside the range of accepted numbers (less than 0 or greater than / equal to 2^64)
     */
    public static EsgAddress fromId(long signedLongId) {
        return new EsgAddress(EsgID.fromLong(signedLongId));
    }

    /**
     * @param unsignedLongId The numeric id that represents this Esg Address
     * @return A EsgAddress object that represents the specified numericId
     * @throws NumberFormatException if the numericId is not a valid number
     * @throws IllegalArgumentException if the numericId is outside the range of accepted numbers (less than 0 or greater than / equal to 2^64)
     */
    public static EsgAddress fromId(String unsignedLongId) {
        return new EsgAddress(EsgID.fromLong(unsignedLongId));
    }

    public static EsgAddress fromRs(String RS) throws IllegalArgumentException {
        if (RS.startsWith("ESG-")) {
            RS = RS.substring(4);
        }
        return new EsgAddress(EsgCrypto.getInstance().rsDecode(RS));
    }

    /**
     * Try to parse an input as either a numeric ID or an RS address.
     *
     * @param input the numeric ID or RS address of the Esg address
     * @return a EsgAddress if one could be parsed from the input, null otherwise
     */
    public static EsgAddress fromEither(String input) {
        if (input == null) return null;
        try {
            return EsgAddress.fromId(EsgID.fromLong(input));
        } catch (IllegalArgumentException e1) {
            try {
                return EsgAddress.fromRs(input);
            } catch (IllegalArgumentException e2) {
                return null;
            }
        }
    }

    /**
     * @return The EsgID of this address
     */
    public EsgID getEsgID() {
        return numericID;
    }

    /**
     * @return The unsigned long numeric ID this EsgAddress points to
     */
    public String getID() {
        return numericID.getID();
    }

    /**
     * @return The signed long numeric ID this EsgAddress points to
     */
    public long getSignedLongId() {
        return numericID.getSignedLongId();
    }

    /**
     * @return The ReedSolomon encoded address, without the "ESG-" prefix
     */
    public String getRawAddress() {
        return address;
    }

    /**
     * @return The ReedSolomon encoded address, with the "ESG-" prefix
     */
    public String getFullAddress() {
        if (address == null || address.length() == 0) {
            return "";
        } else {
            return "ESG-" + address;
        }
    }

    @Override
    public String toString() {
        return getFullAddress();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EsgAddress && Objects.equals(numericID, ((EsgAddress) obj).numericID);
    }

    @Override
    public int hashCode() {
        return numericID.hashCode();
    }
}
