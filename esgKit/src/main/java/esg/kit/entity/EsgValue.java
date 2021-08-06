package esg.kit.entity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class EsgValue implements Comparable<EsgValue> {
    private static final int decimals = 8;

    public static final EsgValue ZERO = EsgValue.fromPlanck(0);
    
    private final BigInteger planck;

    private EsgValue(BigInteger planck) {
        this.planck = planck;
    }

    /**
     * @param planck The number of planck
     * @return The EsgValue representing this number of planck, or a EsgValue representing 0 Esg if the string could not be parsed
     */
    public static EsgValue fromPlanck(String planck) {
        if (planck == null) return ZERO;
        if (planck.toLowerCase(Locale.ENGLISH).endsWith(" planck")) {
            planck = planck.substring(0, planck.length() - 7);
        }
        try {
            return fromPlanck(new BigInteger(planck));
        } catch (NumberFormatException e) {
            return fromPlanck(BigInteger.ZERO);
        }
    }

    /**
     * @param planck The number of planck
     * @return The EsgValue representing this number of planck
     */
    public static EsgValue fromPlanck(long planck) {
        return fromPlanck(BigInteger.valueOf(planck));
    }

    public static EsgValue fromPlanck(BigInteger planck) {
        if (planck == null) return ZERO;
        return new EsgValue(planck);
    }

    /**
     * @param esg The number of esg
     * @return The EsgValue representing this number of esg, or a EsgValue representing 0 Esg if the string could not be parsed
     */
    public static EsgValue fromEsg(String esg) {
        if (esg == null) return ZERO;
        if (esg.toLowerCase(Locale.ENGLISH).endsWith(" esg")) {
            esg = esg.substring(0, esg.length() - 6);
        }
        try {
            return fromEsg(new BigDecimal(esg));
        } catch (NumberFormatException e) {
            return fromPlanck(BigInteger.ZERO);
        }
    }

    /**
     * @param esg The number of esg
     * @return The EsgValue representing this number of esg
     */
    public static EsgValue fromEsg(double esg) {
        return fromEsg(BigDecimal.valueOf(esg));
    }

    public static EsgValue fromEsg(BigDecimal esg) {
        if (esg == null) return ZERO;
        return new EsgValue(esg.multiply(BigDecimal.TEN.pow(decimals)).toBigInteger());
    }

    private static BigDecimal roundToThreeDP(BigDecimal in) {
        if (in.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        } else {
            return in.setScale(3, RoundingMode.HALF_UP).stripTrailingZeros();
        }
    }

    @Override
    public String toString() {
        return toFormattedString();
    }

    /**
     * @return The value with the "ESG" suffix and rounded to 3 decimal places
     */
    public String toFormattedString() {
        return roundToThreeDP(toEsg()).toPlainString() + " ESG";
    }

    /**
     * @return The value without the "ESG" suffix and without rounding
     */
    public String toUnformattedString() {
        return toEsg().stripTrailingZeros().toPlainString();
    }

    /**
     * @return A BigInteger representing the number of planck
     */
    public BigInteger toPlanck() {
        return planck;
    }

    public BigDecimal toEsg() {
        return new BigDecimal(planck, decimals);
    }

    public EsgValue add(EsgValue other) {
        return fromPlanck(planck.add(other.planck));
    }

    public EsgValue subtract(EsgValue other) {
        return fromPlanck(planck.subtract(other.planck));
    }

    public EsgValue multiply(long multiplicand) {
        return fromPlanck(planck.multiply(BigInteger.valueOf(multiplicand)));
    }

    public EsgValue multiply(double multiplicand) {
        return fromEsg(toEsg().multiply(BigDecimal.valueOf(multiplicand)));
    }

    public EsgValue multiply(BigInteger multiplicand) {
        return fromPlanck(planck.multiply(multiplicand));
    }

    public EsgValue multiply(BigDecimal multiplicand) {
        return fromEsg(toEsg().multiply(multiplicand));
    }

    public EsgValue divide(long divisor) {
        return fromPlanck(planck.divide(BigInteger.valueOf(divisor)));
    }

    public EsgValue divide(double divisor) {
        return fromEsg(toEsg().divide(BigDecimal.valueOf(divisor), decimals, RoundingMode.HALF_UP));
    }
    
    public EsgValue divide(BigInteger divisor) {
        return fromPlanck(planck.divide(divisor));
    }

    public EsgValue divide(BigDecimal divisor) {
        return fromEsg(toEsg().divide(divisor, decimals, RoundingMode.HALF_UP));
    }

    public EsgValue abs() {
        return fromPlanck(planck.abs());
    }

    @Override
    public int compareTo(EsgValue other) {
        if (other == null) return 1;
        return planck.compareTo(other.planck);
    }

    public static EsgValue min(EsgValue a, EsgValue b) {
        return (a.compareTo(b) <= 0) ? a : b;
    }

    public static EsgValue max(EsgValue a, EsgValue b) {
        return (a.compareTo(b) >= 0) ? a : b;
    }

    /**
     * @return The number of Esg as a double
     */
    public double doubleValue() { // TODO test
        return toEsg().doubleValue();
    }

    /**
     * @return The number of planck as a long
     */
    public long longValue() { // TODO test
        return toPlanck().longValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EsgValue that = (EsgValue) o;

        return planck != null ? planck.equals(that.planck) : that.planck == null;
    }

    @Override
    public int hashCode() {
        return planck != null ? planck.hashCode() : 0;
    }
}
