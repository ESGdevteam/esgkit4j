package esg.kit.test;

import esg.kit.service.EsgNodeService;
import esg.kit.service.impl.DefaultSchedulerAssigner;
import esg.kit.service.impl.GrpcEsgNodeService;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GrpcEsgNodeServiceTest extends EsgNodeServiceTest {
    @Override
    protected EsgNodeService getEsgNodeService() {
        return new GrpcEsgNodeService("localhost:6878", new DefaultSchedulerAssigner());
    }
}
