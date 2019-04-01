package net.manaty.octopusync.it.fixture.emotiv;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class CortexSocket {

    @OnWebSocketMessage
    public void onTextMessage(Session session, String message) {
        System.err.println(message);
    }
}
