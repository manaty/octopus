package net.manaty.octopusync.it;

import io.bootique.test.junit.BQDaemonTestFactory;
import net.manaty.octopusync.it.fixture.TestServer;
import org.junit.ClassRule;

public abstract class ServerTestBase {

    @ClassRule
    public static BQDaemonTestFactory testFactory = new BQDaemonTestFactory().autoLoadModules();
    @ClassRule
    public static TestServer server = new TestServer(testFactory);
}
