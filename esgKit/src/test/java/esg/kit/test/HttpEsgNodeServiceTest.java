package esg.kit.test;

import esg.kit.service.EsgNodeService;
import esg.kit.service.impl.DefaultSchedulerAssigner;
import esg.kit.service.impl.HttpEsgNodeService;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class HttpEsgNodeServiceTest extends EsgNodeServiceTest {
    @Override
    protected EsgNodeService getEsgNodeService() {
        return new HttpEsgNodeService("https://wallet.esg-alliance.org:8125", "esgkit4j-TEST", new DefaultSchedulerAssigner());
    }
}
