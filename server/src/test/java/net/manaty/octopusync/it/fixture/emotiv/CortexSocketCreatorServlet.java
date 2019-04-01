package net.manaty.octopusync.it.fixture.emotiv;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.annotation.WebServlet;

@WebServlet(loadOnStartup = 1, name = "cortex")
public class CortexSocketCreatorServlet extends WebSocketServlet {
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(CortexSocket.class);
    }
}
