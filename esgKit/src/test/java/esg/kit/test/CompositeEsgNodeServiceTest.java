package esg.kit.test;

import esg.kit.service.EsgNodeService;
import esg.kit.service.impl.CompositeEsgNodeService;
import esg.kit.service.impl.DefaultSchedulerAssigner;
import esg.kit.service.impl.GrpcEsgNodeService;
import esg.kit.service.impl.HttpEsgNodeService;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CompositeEsgNodeServiceTest extends EsgNodeServiceTest {
    @Override
    protected EsgNodeService getEsgNodeService() {
        EsgNodeService http = new HttpEsgNodeService("https://wallet.esg-alliance.org:8125", "esgkit4j-TEST", new DefaultSchedulerAssigner());
        EsgNodeService grpc = new GrpcEsgNodeService("localhost:6878", new DefaultSchedulerAssigner());
        return new CompositeEsgNodeService(http, grpc);
    }
}
