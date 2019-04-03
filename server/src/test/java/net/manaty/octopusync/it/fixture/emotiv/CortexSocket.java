package net.manaty.octopusync.it.fixture.emotiv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.manaty.octopusync.service.emotiv.ResponseErrors;
import net.manaty.octopusync.service.emotiv.message.*;
import org.eclipse.jetty.websocket.api.CloseStatus;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebSocket
public class CortexSocket {
    private static final Logger LOGGER = LoggerFactory.getLogger(CortexSocket.class);

    private final ObjectMapper mapper;

    public CortexSocket() {
        mapper = new ObjectMapper()
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    @OnWebSocketMessage
    public void onTextMessage(Session session, String message) {
        LOGGER.info("Received message: {}", message);
        LoginRequest request;
        if ((request = (LoginRequest) parseRequest(session, message)) != null) {
            LoginResponse response = new LoginResponse();
            response.setId(request.id());
            response.setJsonrpc(request.jsonrpc());
            response.setResult(new Object());
            sendResponse(session, response);
        }
    }

    /**
     * @return parsed request or null in case of error
     */
    private @Nullable Request parseRequest(Session session, String message) {
        JsonNode node;
        int id;

        try {
            node = mapper.readTree(message);
            id = Integer.parseInt(node.get("id").asText());
        } catch (IOException | NumberFormatException e) {
            LOGGER.error("Failed to parse JSON", e);
            session.close(new CloseStatus(HttpServletResponse.SC_BAD_REQUEST, e.getMessage()));
            return null;
        }

        Request request;
        try {
            request = mapper.readValue(node.traverse(), Request.class);
        } catch (IOException e) {
            LOGGER.error("Failed to parse request", e);
            BaseResponse<?> errorResponse = BaseResponse.buildErrorResponse(
                    id, JSONRPC.PROTOCOL_VERSION, ResponseErrors.INVALID_REQUEST.toError());
            sendResponse(session, errorResponse);
            return null;
        }

        return request;
    }

    private void sendResponse(Session session, Response<?> response) {
        String text;
        try {
            text = mapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize response", e);
        }

        try {
            session.getRemote().sendString(text);
        } catch (IOException e) {
            throw new RuntimeException("Failed to send response", e);
        }
    }
}
