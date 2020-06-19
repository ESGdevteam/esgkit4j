package amz.kit.test;

import amz.kit.entity.AmzValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class AmzValueTest {
    @Test
    public void testConstructors() {
        assertEquals("123456789", AmzValue.fromAmz("1.23456789").toPlanck().toString());
        assertEquals("123456789", AmzValue.fromAmz("1.23456789 amz").toPlanck().toString());
        assertEquals("123456789", AmzValue.fromAmz("1.23456789 AMZ").toPlanck().toString());
        assertEquals("123456789", AmzValue.fromAmz(1.23456789).toPlanck().toString());
        assertEquals("123456789", AmzValue.fromAmz(new BigDecimal("1.23456789")).toPlanck().toString());
        assertEquals("123456789", AmzValue.fromPlanck("123456789").toPlanck().toString());
        assertEquals("123456789", AmzValue.fromPlanck("123456789 planck").toPlanck().toString());
        assertEquals("123456789", AmzValue.fromPlanck("123456789 PLANCK").toPlanck().toString());
        assertEquals("123456789", AmzValue.fromPlanck(123456789).toPlanck().toString());
        assertEquals("123456789", AmzValue.fromPlanck(new BigInteger("123456789")).toPlanck().toString());

        // Test null -> 0
        assertEquals(AmzValue.ZERO, AmzValue.fromPlanck((String) null));
        assertEquals(AmzValue.ZERO, AmzValue.fromPlanck((BigInteger) null));
        assertEquals(AmzValue.ZERO, AmzValue.fromAmz((String) null));
        assertEquals(AmzValue.ZERO, AmzValue.fromAmz((BigDecimal) null));
    }

    @Test
    public void testToString() {
        // Positive
        assertEquals("1 AMZ", AmzValue.fromAmz(1).toString());
        assertEquals("1 AMZ", AmzValue.fromAmz(1).toFormattedString());
        assertEquals("1", AmzValue.fromAmz(1).toUnformattedString());
        assertEquals("1 AMZ", AmzValue.fromAmz(1.00000001).toString());
        assertEquals("1 AMZ", AmzValue.fromAmz(1.00000001).toFormattedString());
        assertEquals("1.00000001", AmzValue.fromAmz(1.00000001).toUnformattedString());
        assertEquals("1.235 AMZ", AmzValue.fromPlanck(123456789).toString());
        // Negative
        assertEquals("-1 AMZ", AmzValue.fromAmz(-1).toString());
        assertEquals("-1 AMZ", AmzValue.fromAmz(-1).toFormattedString());
        assertEquals("-1", AmzValue.fromAmz(-1).toUnformattedString());
        assertEquals("-1 AMZ", AmzValue.fromAmz(-1.00000001).toString());
        assertEquals("-1 AMZ", AmzValue.fromAmz(-1.00000001).toFormattedString());
        assertEquals("-1.00000001", AmzValue.fromAmz(-1.00000001).toUnformattedString());
        assertEquals("-1.235 AMZ", AmzValue.fromPlanck(-123456789).toString());
    }

    @Test
    public void testToAmz() {
        assertEquals(BigDecimal.valueOf(100000000, 8), AmzValue.fromAmz(1).toAmz());
        assertEquals(BigDecimal.valueOf(-100000000, 8), AmzValue.fromAmz(-1).toAmz());
    }

    @Test
    public void testAdd() {
        assertEquals(AmzValue.fromAmz(1), AmzValue.fromAmz(0.5).add(AmzValue.fromAmz(0.5)));
        assertEquals(AmzValue.fromAmz(0), AmzValue.fromAmz(-0.5).add(AmzValue.fromAmz(0.5)));
        assertEquals(AmzValue.fromAmz(-1), AmzValue.fromAmz(-0.5).add(AmzValue.fromAmz(-0.5)));
    }

    @Test
    public void testSubtract() {
        assertEquals(AmzValue.fromAmz(1), AmzValue.fromAmz(1.5).subtract(AmzValue.fromAmz(0.5)));
        assertEquals(AmzValue.fromAmz(0), AmzValue.fromAmz(0.5).subtract(AmzValue.fromAmz(0.5)));
        assertEquals(AmzValue.fromAmz(-1), AmzValue.fromAmz(-0.5).subtract(AmzValue.fromAmz(0.5)));
    }

    @Test
    public void testMultiply() {
        // Positive + positive
        assertEquals(AmzValue.fromAmz(10), AmzValue.fromAmz(2).multiply(5));
        assertEquals(AmzValue.fromAmz(10), AmzValue.fromAmz(4).multiply(2.5));
        assertEquals(AmzValue.fromAmz(10), AmzValue.fromAmz(2).multiply(BigInteger.valueOf(5)));
        assertEquals(AmzValue.fromAmz(10), AmzValue.fromAmz(4).multiply(BigDecimal.valueOf(2.5)));

        // Positive + negative
        assertEquals(AmzValue.fromAmz(-10), AmzValue.fromAmz(2).multiply(-5));
        assertEquals(AmzValue.fromAmz(-10), AmzValue.fromAmz(4).multiply(-2.5));
        assertEquals(AmzValue.fromAmz(-10), AmzValue.fromAmz(2).multiply(BigInteger.valueOf(-5)));
        assertEquals(AmzValue.fromAmz(-10), AmzValue.fromAmz(4).multiply(BigDecimal.valueOf(-2.5)));

        // Negative + positive
        assertEquals(AmzValue.fromAmz(-10), AmzValue.fromAmz(-2).multiply(5));
        assertEquals(AmzValue.fromAmz(-10), AmzValue.fromAmz(-4).multiply(2.5));
        assertEquals(AmzValue.fromAmz(-10), AmzValue.fromAmz(-2).multiply(BigInteger.valueOf(5)));
        assertEquals(AmzValue.fromAmz(-10), AmzValue.fromAmz(-4).multiply(BigDecimal.valueOf(2.5)));

        // Negative + negative
        assertEquals(AmzValue.fromAmz(10), AmzValue.fromAmz(-2).multiply(-5));
        assertEquals(AmzValue.fromAmz(10), AmzValue.fromAmz(-4).multiply(-2.5));
        assertEquals(AmzValue.fromAmz(10), AmzValue.fromAmz(-2).multiply(BigInteger.valueOf(-5)));
        assertEquals(AmzValue.fromAmz(10), AmzValue.fromAmz(-4).multiply(BigDecimal.valueOf(-2.5)));
    }
    
    @Test
    public void testDivide() {
        // Positive + positive
        assertEquals(AmzValue.fromAmz(0.4), AmzValue.fromAmz(2).divide(5));
        assertEquals(AmzValue.fromAmz(1.6), AmzValue.fromAmz(4).divide(2.5));
        assertEquals(AmzValue.fromAmz(0.4), AmzValue.fromAmz(2).divide(BigInteger.valueOf(5)));
        assertEquals(AmzValue.fromAmz(1.6), AmzValue.fromAmz(4).divide(BigDecimal.valueOf(2.5)));

        // Positive + negative
        assertEquals(AmzValue.fromAmz(-0.4), AmzValue.fromAmz(2).divide(-5));
        assertEquals(AmzValue.fromAmz(-1.6), AmzValue.fromAmz(4).divide(-2.5));
        assertEquals(AmzValue.fromAmz(-0.4), AmzValue.fromAmz(2).divide(BigInteger.valueOf(-5)));
        assertEquals(AmzValue.fromAmz(-1.6), AmzValue.fromAmz(4).divide(BigDecimal.valueOf(-2.5)));

        // Negative + positive
        assertEquals(AmzValue.fromAmz(-0.4), AmzValue.fromAmz(-2).divide(5));
        assertEquals(AmzValue.fromAmz(-1.6), AmzValue.fromAmz(-4).divide(2.5));
        assertEquals(AmzValue.fromAmz(-0.4), AmzValue.fromAmz(-2).divide(BigInteger.valueOf(5)));
        assertEquals(AmzValue.fromAmz(-1.6), AmzValue.fromAmz(-4).divide(BigDecimal.valueOf(2.5)));

        // Negative + negative
        assertEquals(AmzValue.fromAmz(0.4), AmzValue.fromAmz(-2).divide(-5));
        assertEquals(AmzValue.fromAmz(1.6), AmzValue.fromAmz(-4).divide(-2.5));
        assertEquals(AmzValue.fromAmz(0.4), AmzValue.fromAmz(-2).divide(BigInteger.valueOf(-5)));
        assertEquals(AmzValue.fromAmz(1.6), AmzValue.fromAmz(-4).divide(BigDecimal.valueOf(-2.5)));

        // Recurring divisions
        assertEquals(AmzValue.fromPlanck(33333333), AmzValue.fromAmz(1).divide(3));
        assertEquals(AmzValue.fromPlanck(66666666), AmzValue.fromAmz(2).divide(3));

        // Divisor < 1
        assertEquals(AmzValue.fromAmz(3), AmzValue.fromAmz(1).divide(1.0/3.0));
    }

    @Test
    public void testAbs() {
        assertEquals(AmzValue.fromAmz(1), AmzValue.fromAmz(1).abs());
        assertEquals(AmzValue.fromAmz(1), AmzValue.fromAmz(-1).abs());
        assertEquals(AmzValue.fromAmz(0), AmzValue.fromAmz(0).abs());
    }

    @Test
    public void testMin() {
        assertEquals(AmzValue.fromAmz(1), AmzValue.min(AmzValue.fromAmz(1), AmzValue.fromAmz(2)));
        assertEquals(AmzValue.fromAmz(-2), AmzValue.min(AmzValue.fromAmz(-1), AmzValue.fromAmz(-2)));
    }

    @Test
    public void testMax() {
        assertEquals(AmzValue.fromAmz(2), AmzValue.max(AmzValue.fromAmz(1), AmzValue.fromAmz(2)));
        assertEquals(AmzValue.fromAmz(-1), AmzValue.max(AmzValue.fromAmz(-1), AmzValue.fromAmz(-2)));
    }
}
