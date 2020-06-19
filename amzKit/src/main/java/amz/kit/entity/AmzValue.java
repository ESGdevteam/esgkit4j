package amz.kit.entity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class AmzValue implements Comparable<AmzValue> {
    private static final int decimals = 8;

    public static final AmzValue ZERO = AmzValue.fromPlanck(0);
    
    private final BigInteger planck;

    private AmzValue(BigInteger planck) {
        this.planck = planck;
    }

    /**
     * @param planck The number of planck
     * @return The AmzValue representing this number of planck, or a AmzValue representing 0 Amz if the string could not be parsed
     */
    public static AmzValue fromPlanck(String planck) {
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
     * @return The AmzValue representing this number of planck
     */
    public static AmzValue fromPlanck(long planck) {
        return fromPlanck(BigInteger.valueOf(planck));
    }

    public static AmzValue fromPlanck(BigInteger planck) {
        if (planck == null) return ZERO;
        return new AmzValue(planck);
    }

    /**
     * @param amz The number of amz
     * @return The AmzValue representing this number of amz, or a AmzValue representing 0 Amz if the string could not be parsed
     */
    public static AmzValue fromAmz(String amz) {
        if (amz == null) return ZERO;
        if (amz.toLowerCase(Locale.ENGLISH).endsWith(" amz")) {
            amz = amz.substring(0, amz.length() - 6);
        }
        try {
            return fromAmz(new BigDecimal(amz));
        } catch (NumberFormatException e) {
            return fromPlanck(BigInteger.ZERO);
        }
    }

    /**
     * @param amz The number of amz
     * @return The AmzValue representing this number of amz
     */
    public static AmzValue fromAmz(double amz) {
        return fromAmz(BigDecimal.valueOf(amz));
    }

    public static AmzValue fromAmz(BigDecimal amz) {
        if (amz == null) return ZERO;
        return new AmzValue(amz.multiply(BigDecimal.TEN.pow(decimals)).toBigInteger());
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
     * @return The value with the "AMZ" suffix and rounded to 3 decimal places
     */
    public String toFormattedString() {
        return roundToThreeDP(toAmz()).toPlainString() + " AMZ";
    }

    /**
     * @return The value without the "AMZ" suffix and without rounding
     */
    public String toUnformattedString() {
        return toAmz().stripTrailingZeros().toPlainString();
    }

    /**
     * @return A BigInteger representing the number of planck
     */
    public BigInteger toPlanck() {
        return planck;
    }

    public BigDecimal toAmz() {
        return new BigDecimal(planck, decimals);
    }

    public AmzValue add(AmzValue other) {
        return fromPlanck(planck.add(other.planck));
    }

    public AmzValue subtract(AmzValue other) {
        return fromPlanck(planck.subtract(other.planck));
    }

    public AmzValue multiply(long multiplicand) {
        return fromPlanck(planck.multiply(BigInteger.valueOf(multiplicand)));
    }

    public AmzValue multiply(double multiplicand) {
        return fromAmz(toAmz().multiply(BigDecimal.valueOf(multiplicand)));
    }

    public AmzValue multiply(BigInteger multiplicand) {
        return fromPlanck(planck.multiply(multiplicand));
    }

    public AmzValue multiply(BigDecimal multiplicand) {
        return fromAmz(toAmz().multiply(multiplicand));
    }

    public AmzValue divide(long divisor) {
        return fromPlanck(planck.divide(BigInteger.valueOf(divisor)));
    }

    public AmzValue divide(double divisor) {
        return fromAmz(toAmz().divide(BigDecimal.valueOf(divisor), decimals, RoundingMode.HALF_UP));
    }
    
    public AmzValue divide(BigInteger divisor) {
        return fromPlanck(planck.divide(divisor));
    }

    public AmzValue divide(BigDecimal divisor) {
        return fromAmz(toAmz().divide(divisor, decimals, RoundingMode.HALF_UP));
    }

    public AmzValue abs() {
        return fromPlanck(planck.abs());
    }

    @Override
    public int compareTo(AmzValue other) {
        if (other == null) return 1;
        return planck.compareTo(other.planck);
    }

    public static AmzValue min(AmzValue a, AmzValue b) {
        return (a.compareTo(b) <= 0) ? a : b;
    }

    public static AmzValue max(AmzValue a, AmzValue b) {
        return (a.compareTo(b) >= 0) ? a : b;
    }

    /**
     * @return The number of Amz as a double
     */
    public double doubleValue() { // TODO test
        return toAmz().doubleValue();
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

        AmzValue that = (AmzValue) o;

        return planck != null ? planck.equals(that.planck) : that.planck == null;
    }

    @Override
    public int hashCode() {
        return planck != null ? planck.hashCode() : 0;
    }
}
