package amz.kit.test;

import amz.kit.service.AmzNodeService;
import amz.kit.service.impl.CompositeAmzNodeService;
import amz.kit.service.impl.DefaultSchedulerAssigner;
import amz.kit.service.impl.GrpcAmzNodeService;
import amz.kit.service.impl.HttpAmzNodeService;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CompositeAmzNodeServiceTest extends AmzNodeServiceTest {
    @Override
    protected AmzNodeService getAmzNodeService() {
        AmzNodeService http = new HttpAmzNodeService("https://wallet.amz-alliance.org:8125", "amzkit4j-TEST", new DefaultSchedulerAssigner());
        AmzNodeService grpc = new GrpcAmzNodeService("localhost:6878", new DefaultSchedulerAssigner());
        return new CompositeAmzNodeService(http, grpc);
    }
}
