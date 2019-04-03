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
import org.slf4j.MDC;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

// LIMITATION: will work incorrectly if there are concurrent sessions with the same username
@WebSocket
public class CortexSocket {
    private static final Logger LOGGER = LoggerFactory.getLogger(CortexSocket.class);

    private static class UserInfo {
        volatile String username;
        volatile String authToken;
    }

    private final ObjectMapper mapper;
    private final Map<Class<? extends Request>, BiConsumer<Session, Request>> requestProcessors;

    private final ConcurrentMap<Session, UserInfo> usersBySessions;

    public CortexSocket() {
        this.mapper = new ObjectMapper()
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        this.requestProcessors = buildRequestProcessors();

        // state
        this.usersBySessions = new ConcurrentHashMap<>();
    }

    private Map<Class<? extends Request>, BiConsumer<Session, Request>> buildRequestProcessors() {
        Map<Class<? extends Request>, BiConsumer<Session, Request>> m = new HashMap<>();
        m.put(GetUserLoginRequest.class, (session, request) -> onGetUserLoginRequest(session, (GetUserLoginRequest) request));
        m.put(LoginRequest.class, (session, request) -> onLoginRequest(session, (LoginRequest) request));
        m.put(AuthorizeRequest.class, (session, request) -> onAuthorizeRequest(session, (AuthorizeRequest) request));
        m.put(SubscribeRequest.class, (session, request) -> onSubscribeRequest(session, (SubscribeRequest) request));
        return m;
    }

    @OnWebSocketMessage
    public void onTextMessage(Session session, String message) {
        MDC.put("WSRemoteAddress", session.getRemoteAddress().toString());
        try {
            LOGGER.info("Received message: {}", message);

            Request request;
            if ((request = parseRequest(session, message)) != null) {
                try {
                    requestProcessors.getOrDefault(request.getClass(), this::onUnsupportedRequest)
                            .accept(session, request);
                } catch (Exception e) {
                    LOGGER.error("Internal error", e);
                    sendResponse(session, BaseResponse.buildErrorResponse(
                            request.id(), JSONRPC.PROTOCOL_VERSION, ResponseErrors.UNKNOWN_ERROR.toError()));
                }
            }
        } finally {
            MDC.remove("WSRemoteAddress");
        }
    }

    private void onGetUserLoginRequest(Session session, GetUserLoginRequest request) {
        List<String> loggedInUsers = usersBySessions.values().stream()
                .map(u -> u.username)
                .collect(Collectors.toList());

        GetUserLoginResponse response = new GetUserLoginResponse();
        response.setId(request.id());
        response.setJsonrpc(JSONRPC.PROTOCOL_VERSION);
        response.setResult(loggedInUsers);
        sendResponse(session, response);
    }

    private void onLoginRequest(Session session, LoginRequest request) {
        Response<?> response;

        Map<String, Object> params = Objects.requireNonNull(request.params());
        String username = Objects.requireNonNull((String) params.get("username"));
        Objects.requireNonNull((String) params.get("password"));
        Objects.requireNonNull((String) params.get("client_id"));
        Objects.requireNonNull((String) params.get("client_secret"));

        UserInfo userInfo = usersBySessions.get(session);
        if (userInfo != null) {
            LOGGER.error("Login request with username {}. Session is already authenticated as {}.",
                    username, userInfo.username);
            response = BaseResponse.buildErrorResponse(
                    request.id(), JSONRPC.PROTOCOL_VERSION, ResponseErrors.LOGOUT_REQUIRED_BEFORE_LOGIN.toError());
        } else {
            response = new LoginResponse();
            ((LoginResponse) response).setId(request.id());
            ((LoginResponse) response).setJsonrpc(JSONRPC.PROTOCOL_VERSION);
        }

        sendResponse(session, response);
    }

    private void onAuthorizeRequest(Session session, AuthorizeRequest request) {
        Response<?> response;

        Map<String, Object> params = Objects.requireNonNull(request.params());
        Objects.requireNonNull((String) params.get("client_id"));
        Objects.requireNonNull((String) params.get("client_secret"));
        Objects.requireNonNull((Integer) params.get("debit"));

        UserInfo userInfo = usersBySessions.get(session);
        if (userInfo == null) {
            response = BaseResponse.buildErrorResponse(
                    request.id(), JSONRPC.PROTOCOL_VERSION, ResponseErrors.LOGIN_REQUIRED_TO_AUTHORIZE.toError());
        } else if (userInfo.authToken != null) {
            LOGGER.error("Username {} is already issued an authorization token", userInfo.username);
            response = BaseResponse.buildErrorResponse(
                    request.id(), JSONRPC.PROTOCOL_VERSION, ResponseErrors.UNKNOWN_ERROR.toError());
        } else {
            String token = UUID.randomUUID().toString();
            userInfo.authToken = token;

            response = new AuthorizeResponse();
            ((AuthorizeResponse) response).setId(request.id());
            ((AuthorizeResponse) response).setJsonrpc(JSONRPC.PROTOCOL_VERSION);
            AuthorizeResponse.AuthTokenHolder tokenHolder = new AuthorizeResponse.AuthTokenHolder();
            tokenHolder.setToken(token);
            ((AuthorizeResponse) response).setResult(tokenHolder);
        }

        sendResponse(session, response);
    }

    private void onSubscribeRequest(Session session, SubscribeRequest request) {
        throw new UnsupportedOperationException();
    }

    private void onUnsupportedRequest(Session session, Request request) {
        LOGGER.error("Unsupported request type: " + request.getClass());
        BaseResponse<?> errorResponse = BaseResponse.buildErrorResponse(
                request.id(), JSONRPC.PROTOCOL_VERSION, ResponseErrors.UNKNOWN_METHOD.toError());
        sendResponse(session, errorResponse);
    }

    /**
     * @return parsed request or null in case of error
     */
    private @Nullable Request parseRequest(Session session, String message) {
        JsonNode node;
        long id;

        try {
            node = mapper.readTree(message);
            id = Long.parseLong(node.get("id").asText());
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
