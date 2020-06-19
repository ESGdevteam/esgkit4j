package amz.kit.test.crypto.plot;

import amz.kit.crypto.AmzCrypto;
import amz.kit.crypto.plot.PlotCalculator;
import amz.kit.crypto.plot.impl.PlotCalculatorImpl;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PlotCalculatorImplTest extends PlotCalculatorTest {
    @Override
    protected PlotCalculator getPlotCalculator() {
        return new PlotCalculatorImpl(() -> AmzCrypto.getInstance().getShabal256());
    }
}
