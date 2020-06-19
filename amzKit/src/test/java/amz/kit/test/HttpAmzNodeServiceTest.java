package amz.kit.test;

import amz.kit.service.AmzNodeService;
import amz.kit.service.impl.DefaultSchedulerAssigner;
import amz.kit.service.impl.HttpAmzNodeService;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class HttpAmzNodeServiceTest extends AmzNodeServiceTest {
    @Override
    protected AmzNodeService getAmzNodeService() {
        return new HttpAmzNodeService("https://wallet.amz-alliance.org:8125", "amzkit4j-TEST", new DefaultSchedulerAssigner());
    }
}
