package amz.kit.test;

import amz.kit.service.AmzNodeService;
import amz.kit.service.impl.DefaultSchedulerAssigner;
import amz.kit.service.impl.GrpcAmzNodeService;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GrpcAmzNodeServiceTest extends AmzNodeServiceTest {
    @Override
    protected AmzNodeService getAmzNodeService() {
        return new GrpcAmzNodeService("localhost:6878", new DefaultSchedulerAssigner());
    }
}
