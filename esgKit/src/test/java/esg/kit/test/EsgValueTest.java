package esg.kit.test;

import esg.kit.entity.EsgValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class EsgValueTest {
    @Test
    public void testConstructors() {
        assertEquals("123456789", EsgValue.fromEsg("1.23456789").toPlanck().toString());
        assertEquals("123456789", EsgValue.fromEsg("1.23456789 esg").toPlanck().toString());
        assertEquals("123456789", EsgValue.fromEsg("1.23456789 ESG").toPlanck().toString());
        assertEquals("123456789", EsgValue.fromEsg(1.23456789).toPlanck().toString());
        assertEquals("123456789", EsgValue.fromEsg(new BigDecimal("1.23456789")).toPlanck().toString());
        assertEquals("123456789", EsgValue.fromPlanck("123456789").toPlanck().toString());
        assertEquals("123456789", EsgValue.fromPlanck("123456789 planck").toPlanck().toString());
        assertEquals("123456789", EsgValue.fromPlanck("123456789 PLANCK").toPlanck().toString());
        assertEquals("123456789", EsgValue.fromPlanck(123456789).toPlanck().toString());
        assertEquals("123456789", EsgValue.fromPlanck(new BigInteger("123456789")).toPlanck().toString());

        // Test null -> 0
        assertEquals(EsgValue.ZERO, EsgValue.fromPlanck((String) null));
        assertEquals(EsgValue.ZERO, EsgValue.fromPlanck((BigInteger) null));
        assertEquals(EsgValue.ZERO, EsgValue.fromEsg((String) null));
        assertEquals(EsgValue.ZERO, EsgValue.fromEsg((BigDecimal) null));
    }

    @Test
    public void testToString() {
        // Positive
        assertEquals("1 ESG", EsgValue.fromEsg(1).toString());
        assertEquals("1 ESG", EsgValue.fromEsg(1).toFormattedString());
        assertEquals("1", EsgValue.fromEsg(1).toUnformattedString());
        assertEquals("1 ESG", EsgValue.fromEsg(1.00000001).toString());
        assertEquals("1 ESG", EsgValue.fromEsg(1.00000001).toFormattedString());
        assertEquals("1.00000001", EsgValue.fromEsg(1.00000001).toUnformattedString());
        assertEquals("1.235 ESG", EsgValue.fromPlanck(123456789).toString());
        // Negative
        assertEquals("-1 ESG", EsgValue.fromEsg(-1).toString());
        assertEquals("-1 ESG", EsgValue.fromEsg(-1).toFormattedString());
        assertEquals("-1", EsgValue.fromEsg(-1).toUnformattedString());
        assertEquals("-1 ESG", EsgValue.fromEsg(-1.00000001).toString());
        assertEquals("-1 ESG", EsgValue.fromEsg(-1.00000001).toFormattedString());
        assertEquals("-1.00000001", EsgValue.fromEsg(-1.00000001).toUnformattedString());
        assertEquals("-1.235 ESG", EsgValue.fromPlanck(-123456789).toString());
    }

    @Test
    public void testToEsg() {
        assertEquals(BigDecimal.valueOf(100000000, 8), EsgValue.fromEsg(1).toEsg());
        assertEquals(BigDecimal.valueOf(-100000000, 8), EsgValue.fromEsg(-1).toEsg());
    }

    @Test
    public void testAdd() {
        assertEquals(EsgValue.fromEsg(1), EsgValue.fromEsg(0.5).add(EsgValue.fromEsg(0.5)));
        assertEquals(EsgValue.fromEsg(0), EsgValue.fromEsg(-0.5).add(EsgValue.fromEsg(0.5)));
        assertEquals(EsgValue.fromEsg(-1), EsgValue.fromEsg(-0.5).add(EsgValue.fromEsg(-0.5)));
    }

    @Test
    public void testSubtract() {
        assertEquals(EsgValue.fromEsg(1), EsgValue.fromEsg(1.5).subtract(EsgValue.fromEsg(0.5)));
        assertEquals(EsgValue.fromEsg(0), EsgValue.fromEsg(0.5).subtract(EsgValue.fromEsg(0.5)));
        assertEquals(EsgValue.fromEsg(-1), EsgValue.fromEsg(-0.5).subtract(EsgValue.fromEsg(0.5)));
    }

    @Test
    public void testMultiply() {
        // Positive + positive
        assertEquals(EsgValue.fromEsg(10), EsgValue.fromEsg(2).multiply(5));
        assertEquals(EsgValue.fromEsg(10), EsgValue.fromEsg(4).multiply(2.5));
        assertEquals(EsgValue.fromEsg(10), EsgValue.fromEsg(2).multiply(BigInteger.valueOf(5)));
        assertEquals(EsgValue.fromEsg(10), EsgValue.fromEsg(4).multiply(BigDecimal.valueOf(2.5)));

        // Positive + negative
        assertEquals(EsgValue.fromEsg(-10), EsgValue.fromEsg(2).multiply(-5));
        assertEquals(EsgValue.fromEsg(-10), EsgValue.fromEsg(4).multiply(-2.5));
        assertEquals(EsgValue.fromEsg(-10), EsgValue.fromEsg(2).multiply(BigInteger.valueOf(-5)));
        assertEquals(EsgValue.fromEsg(-10), EsgValue.fromEsg(4).multiply(BigDecimal.valueOf(-2.5)));

        // Negative + positive
        assertEquals(EsgValue.fromEsg(-10), EsgValue.fromEsg(-2).multiply(5));
        assertEquals(EsgValue.fromEsg(-10), EsgValue.fromEsg(-4).multiply(2.5));
        assertEquals(EsgValue.fromEsg(-10), EsgValue.fromEsg(-2).multiply(BigInteger.valueOf(5)));
        assertEquals(EsgValue.fromEsg(-10), EsgValue.fromEsg(-4).multiply(BigDecimal.valueOf(2.5)));

        // Negative + negative
        assertEquals(EsgValue.fromEsg(10), EsgValue.fromEsg(-2).multiply(-5));
        assertEquals(EsgValue.fromEsg(10), EsgValue.fromEsg(-4).multiply(-2.5));
        assertEquals(EsgValue.fromEsg(10), EsgValue.fromEsg(-2).multiply(BigInteger.valueOf(-5)));
        assertEquals(EsgValue.fromEsg(10), EsgValue.fromEsg(-4).multiply(BigDecimal.valueOf(-2.5)));
    }
    
    @Test
    public void testDivide() {
        // Positive + positive
        assertEquals(EsgValue.fromEsg(0.4), EsgValue.fromEsg(2).divide(5));
        assertEquals(EsgValue.fromEsg(1.6), EsgValue.fromEsg(4).divide(2.5));
        assertEquals(EsgValue.fromEsg(0.4), EsgValue.fromEsg(2).divide(BigInteger.valueOf(5)));
        assertEquals(EsgValue.fromEsg(1.6), EsgValue.fromEsg(4).divide(BigDecimal.valueOf(2.5)));

        // Positive + negative
        assertEquals(EsgValue.fromEsg(-0.4), EsgValue.fromEsg(2).divide(-5));
        assertEquals(EsgValue.fromEsg(-1.6), EsgValue.fromEsg(4).divide(-2.5));
        assertEquals(EsgValue.fromEsg(-0.4), EsgValue.fromEsg(2).divide(BigInteger.valueOf(-5)));
        assertEquals(EsgValue.fromEsg(-1.6), EsgValue.fromEsg(4).divide(BigDecimal.valueOf(-2.5)));

        // Negative + positive
        assertEquals(EsgValue.fromEsg(-0.4), EsgValue.fromEsg(-2).divide(5));
        assertEquals(EsgValue.fromEsg(-1.6), EsgValue.fromEsg(-4).divide(2.5));
        assertEquals(EsgValue.fromEsg(-0.4), EsgValue.fromEsg(-2).divide(BigInteger.valueOf(5)));
        assertEquals(EsgValue.fromEsg(-1.6), EsgValue.fromEsg(-4).divide(BigDecimal.valueOf(2.5)));

        // Negative + negative
        assertEquals(EsgValue.fromEsg(0.4), EsgValue.fromEsg(-2).divide(-5));
        assertEquals(EsgValue.fromEsg(1.6), EsgValue.fromEsg(-4).divide(-2.5));
        assertEquals(EsgValue.fromEsg(0.4), EsgValue.fromEsg(-2).divide(BigInteger.valueOf(-5)));
        assertEquals(EsgValue.fromEsg(1.6), EsgValue.fromEsg(-4).divide(BigDecimal.valueOf(-2.5)));

        // Recurring divisions
        assertEquals(EsgValue.fromPlanck(33333333), EsgValue.fromEsg(1).divide(3));
        assertEquals(EsgValue.fromPlanck(66666666), EsgValue.fromEsg(2).divide(3));

        // Divisor < 1
        assertEquals(EsgValue.fromEsg(3), EsgValue.fromEsg(1).divide(1.0/3.0));
    }

    @Test
    public void testAbs() {
        assertEquals(EsgValue.fromEsg(1), EsgValue.fromEsg(1).abs());
        assertEquals(EsgValue.fromEsg(1), EsgValue.fromEsg(-1).abs());
        assertEquals(EsgValue.fromEsg(0), EsgValue.fromEsg(0).abs());
    }

    @Test
    public void testMin() {
        assertEquals(EsgValue.fromEsg(1), EsgValue.min(EsgValue.fromEsg(1), EsgValue.fromEsg(2)));
        assertEquals(EsgValue.fromEsg(-2), EsgValue.min(EsgValue.fromEsg(-1), EsgValue.fromEsg(-2)));
    }

    @Test
    public void testMax() {
        assertEquals(EsgValue.fromEsg(2), EsgValue.max(EsgValue.fromEsg(1), EsgValue.fromEsg(2)));
        assertEquals(EsgValue.fromEsg(-1), EsgValue.max(EsgValue.fromEsg(-1), EsgValue.fromEsg(-2)));
    }
}
