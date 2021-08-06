package esg.kit.test.crypto.plot;

import esg.kit.crypto.EsgCrypto;
import esg.kit.crypto.plot.PlotCalculator;
import esg.kit.crypto.plot.impl.PlotCalculatorImpl;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PlotCalculatorImplTest extends PlotCalculatorTest {
    @Override
    protected PlotCalculator getPlotCalculator() {
        return new PlotCalculatorImpl(() -> EsgCrypto.getInstance().getShabal256());
    }
}
