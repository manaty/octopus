package net.manaty.octopusync.it.fixture.emotiv;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;

@WebServlet(loadOnStartup = 1, name = "cortex")
public class CortexSocketCreatorServlet extends WebSocketServlet {

    private CortexInfoService cortexInfoService;

    @Inject
    public CortexSocketCreatorServlet(CortexInfoService cortexInfoService) {
        this.cortexInfoService = cortexInfoService;
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.setCreator((req, resp) -> new CortexSocket(cortexInfoService));
    }
}
