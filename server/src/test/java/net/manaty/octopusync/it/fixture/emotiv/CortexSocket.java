package net.manaty.octopusync.it.fixture.emotiv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import net.manaty.octopusync.it.fixture.emotiv.CortexEventSubscriptionService.CortexEventSubscription;
import net.manaty.octopusync.service.emotiv.ResponseErrors;
import net.manaty.octopusync.service.emotiv.event.CortexEventKind;
import net.manaty.octopusync.service.emotiv.message.*;
import org.eclipse.jetty.websocket.api.CloseStatus;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;

// LIMITATION: will work incorrectly if there are concurrent sessions with the same username or client ID
@WebSocket
public class CortexSocket {
    private static final Logger LOGGER = LoggerFactory.getLogger(CortexSocket.class);

    private final ObjectMapper mapper;
    private final Map<Class<? extends Request>,
            BiConsumer<org.eclipse.jetty.websocket.api.Session, Request>> requestProcessors;

    private final CortexInfoService cortexInfoService;
    private final CortexEventSubscriptionService subscriptionService;

    public CortexSocket(CortexInfoService cortexInfoService, CortexEventSubscriptionService subscriptionService) {
        this.mapper = new ObjectMapper()
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        this.requestProcessors = buildRequestProcessors();
        this.cortexInfoService = cortexInfoService;
        this.subscriptionService = subscriptionService;
    }

    private Map<Class<? extends Request>, BiConsumer<org.eclipse.jetty.websocket.api.Session, Request>> buildRequestProcessors() {
        Map<Class<? extends Request>, BiConsumer<org.eclipse.jetty.websocket.api.Session, Request>> m = new HashMap<>();
        m.put(GetUserLoginRequest.class, (session, request) -> onGetUserLoginRequest(session, (GetUserLoginRequest) request));
        m.put(LoginRequest.class, (session, request) -> onLoginRequest(session, (LoginRequest) request));
        m.put(LogoutRequest.class, (session, request) -> onLogoutRequest(session, (LogoutRequest) request));
        m.put(AuthorizeRequest.class, (session, request) -> onAuthorizeRequest(session, (AuthorizeRequest) request));
        m.put(QuerySessionsRequest.class, ((session, request) -> onQuerySessionsRequest(session, (QuerySessionsRequest) request)));
        m.put(CreateSessionRequest.class, ((session, request) -> onCreateSessionRequest(session, (CreateSessionRequest) request)));
        m.put(UpdateSessionRequest.class, ((session, request) -> onUpdateSessionRequest(session, (UpdateSessionRequest) request)));
        m.put(SubscribeRequest.class, (session, request) -> onSubscribeRequest(session, (SubscribeRequest) request));
        return m;
    }

    @SuppressWarnings("unused")
    @OnWebSocketMessage
    public void onTextMessage(org.eclipse.jetty.websocket.api.Session session, String message) {
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

    private void onGetUserLoginRequest(org.eclipse.jetty.websocket.api.Session session, GetUserLoginRequest request) {
        List<String> loggedInUsers = cortexInfoService.getLoggedInUsers();

        GetUserLoginResponse response = new GetUserLoginResponse();
        response.setId(request.id());
        response.setJsonrpc(JSONRPC.PROTOCOL_VERSION);
        response.setResult(loggedInUsers);
        sendResponse(session, response);
    }

    private void onLoginRequest(org.eclipse.jetty.websocket.api.Session session, LoginRequest request) {
        Response<?> response;

        Map<String, Object> params = Objects.requireNonNull(request.params());
        String username = Objects.requireNonNull((String) params.get("username"));
        String password = Objects.requireNonNull((String) params.get("password"));
        String clientId = Objects.requireNonNull((String) params.get("client_id"));
        String clientSecret = Objects.requireNonNull((String) params.get("client_secret"));

        UserInfo existingUserInfo = cortexInfoService.getUserInfoByClientId(clientId);
        if (existingUserInfo != null) {
            LOGGER.error("Login request with username {}. Client is already authenticated as {}.",
                    username, existingUserInfo.getUsername());
            response = BaseResponse.buildErrorResponse(
                    request.id(), JSONRPC.PROTOCOL_VERSION, ResponseErrors.LOGOUT_REQUIRED_BEFORE_LOGIN.toError());
        } else {
            List<String> loggedInUsers = cortexInfoService.getLoggedInUsers();
            if (loggedInUsers.isEmpty()) {
                TestCortexCredentials credentials = cortexInfoService.getCredentialsForUsername(username);
                if (credentials == null || !credentials.getPassword().equals(password)) {
                    LOGGER.error("Login request with invalid username/password: {}/{}", username, password);
                    response = BaseResponse.buildErrorResponse(
                            request.id(), JSONRPC.PROTOCOL_VERSION, ResponseErrors.INVALID_CREDENTIALS.toError());
                    sendResponse(session, response);
                } else if (!credentials.getClientId().equals(clientId) ||
                        !credentials.getClientSecret().equals(clientSecret)) {

                    LOGGER.error("Login request with invalid client ID/secret: {}", username);
                    response = BaseResponse.buildErrorResponse(
                            request.id(), JSONRPC.PROTOCOL_VERSION, ResponseErrors.INVALID_CLIENT_ID_OR_SECRET.toError());
                    sendResponse(session, response);
                } else {
                    cortexInfoService.login(clientId, username);

                    response = new LoginResponse();
                    ((LoginResponse) response).setId(request.id());
                    ((LoginResponse) response).setJsonrpc(JSONRPC.PROTOCOL_VERSION);
                }
            } else if (loggedInUsers.contains(username)) {
                LOGGER.error("Login request with username {}. User info not found -- must be a bug.", username);
                response = BaseResponse.buildErrorResponse(
                        request.id(), JSONRPC.PROTOCOL_VERSION, ResponseErrors.UNKNOWN_ERROR.toError());
            } else {
                LOGGER.error("Login request with username {}. There are other users that are logged in: {}",
                        username, loggedInUsers);
                response = BaseResponse.buildErrorResponse(
                        request.id(), JSONRPC.PROTOCOL_VERSION, ResponseErrors.LOGOUT_REQUIRED_BEFORE_LOGIN.toError());
            }
        }

        sendResponse(session, response);
    }

    private void onLogoutRequest(org.eclipse.jetty.websocket.api.Session session, LogoutRequest request) {
        Response<?> response;

        Map<String, Object> params = Objects.requireNonNull(request.params());
        String username = Objects.requireNonNull((String) params.get("username"));

        if (cortexInfoService.logout(username)) {
            response = new LogoutResponse();
            ((LogoutResponse) response).setId(request.id());
            ((LogoutResponse) response).setJsonrpc(JSONRPC.PROTOCOL_VERSION);
        } else {
            LOGGER.error("Logout request with username {}. User is not logged in.", username);
            response = BaseResponse.buildErrorResponse(
                    request.id(), JSONRPC.PROTOCOL_VERSION, ResponseErrors.UNKNOWN_ERROR.toError());
        }

        sendResponse(session, response);
    }

    private void onAuthorizeRequest(org.eclipse.jetty.websocket.api.Session session, AuthorizeRequest request) {
        Response<?> response;

        Map<String, Object> params = Objects.requireNonNull(request.params());
        String clientId = Objects.requireNonNull((String) params.get("client_id"));
        Objects.requireNonNull((String) params.get("client_secret"));
        Objects.requireNonNull((Integer) params.get("debit"));

        UserInfo userInfo = cortexInfoService.getUserInfoByClientId(clientId);
        if (userInfo == null) {
            LOGGER.error("Missing authentication");
            response = BaseResponse.buildErrorResponse(
                    request.id(), JSONRPC.PROTOCOL_VERSION, ResponseErrors.LOGIN_REQUIRED_TO_AUTHORIZE.toError());
        } else {
            String token = UUID.randomUUID().toString();
            userInfo.setAuthToken(token);

            response = new AuthorizeResponse();
            ((AuthorizeResponse) response).setId(request.id());
            ((AuthorizeResponse) response).setJsonrpc(JSONRPC.PROTOCOL_VERSION);
            AuthorizeResponse.AuthTokenHolder tokenHolder = new AuthorizeResponse.AuthTokenHolder();
            tokenHolder.setToken(token);
            ((AuthorizeResponse) response).setResult(tokenHolder);
        }

        sendResponse(session, response);
    }

    private void onQuerySessionsRequest(org.eclipse.jetty.websocket.api.Session session, QuerySessionsRequest request) {
        Response<?> response;

        String authzToken = Objects.requireNonNull((String) request.params().get("_auth"));
        if (cortexInfoService.getUserInfoByAuthzToken(authzToken) == null) {
            LOGGER.error("Invalid authz token");
            response = BaseResponse.buildErrorResponse(
                    request.id(), JSONRPC.PROTOCOL_VERSION, ResponseErrors.INVALID_AUTH_TOKEN.toError());
        } else {
            response = new QuerySessionsResponse();
            ((QuerySessionsResponse) response).setId(request.id());
            ((QuerySessionsResponse) response).setJsonrpc(JSONRPC.PROTOCOL_VERSION);
            ((QuerySessionsResponse) response).setResult(cortexInfoService.getSessions());
        }

        sendResponse(session, response);
    }

    private void onCreateSessionRequest(org.eclipse.jetty.websocket.api.Session session, CreateSessionRequest request) {
        Response<?> response;

        String authzToken = Objects.requireNonNull((String) request.params().get("_auth"));
        if (cortexInfoService.getUserInfoByAuthzToken(authzToken) == null) {
            LOGGER.error("Invalid authz token");
            response = BaseResponse.buildErrorResponse(
                    request.id(), JSONRPC.PROTOCOL_VERSION, ResponseErrors.INVALID_AUTH_TOKEN.toError());
        } else {
            String headsetId = Objects.requireNonNull((String) request.params().get("headset"));
            Session.Status status = Session.Status.forName(Objects.requireNonNull((String) request.params().get("status")));

            List<Session> sessions = cortexInfoService.getSessions();
            boolean sessionExists = sessions.stream()
                    .anyMatch(s -> s.getHeadset().getId().equals(headsetId));
            if (sessionExists) {
                LOGGER.error("Session for headset ID {} already exists", headsetId);
                response = BaseResponse.buildErrorResponse(
                        request.id(), JSONRPC.PROTOCOL_VERSION, ResponseErrors.DUPLICATE_SESSION_FOR_DEVICE.toError());
            } else {
                response = new CreateSessionResponse();
                ((CreateSessionResponse) response).setId(request.id());
                ((CreateSessionResponse) response).setJsonrpc(JSONRPC.PROTOCOL_VERSION);
                ((CreateSessionResponse) response).setResult(cortexInfoService.createSession(authzToken, headsetId, status));
            }
        }

        sendResponse(session, response);
    }

    private void onUpdateSessionRequest(org.eclipse.jetty.websocket.api.Session session, UpdateSessionRequest request) {
        Response<?> response;

        String authzToken = Objects.requireNonNull((String) request.params().get("_auth"));
        if (cortexInfoService.getUserInfoByAuthzToken(authzToken) == null) {
            LOGGER.error("Invalid authz token");
            response = BaseResponse.buildErrorResponse(
                    request.id(), JSONRPC.PROTOCOL_VERSION, ResponseErrors.INVALID_AUTH_TOKEN.toError());
        } else {
            String sessionId = Objects.requireNonNull((String) request.params().get("session"));
            Session.Status status = Session.Status.forName(Objects.requireNonNull((String) request.params().get("status")));

            List<Session> sessions = cortexInfoService.getSessions();
            Session existingSession = sessions.stream()
                    .filter(s -> s.getId().equals(sessionId))
                    .findAny().orElse(null);

            if (existingSession == null) {
                LOGGER.error("Session with ID {} does not exist", sessionId);
                response = BaseResponse.buildErrorResponse(
                        request.id(), JSONRPC.PROTOCOL_VERSION, ResponseErrors.SESSION_DOES_NOT_EXIST.toError());
            } else {
                response = new UpdateSessionResponse();
                ((UpdateSessionResponse) response).setId(request.id());
                ((UpdateSessionResponse) response).setJsonrpc(JSONRPC.PROTOCOL_VERSION);
                ((UpdateSessionResponse) response).setResult(cortexInfoService.updateSession(authzToken, existingSession.getId(), status));
            }
        }

        sendResponse(session, response);
    }

    private void onSubscribeRequest(org.eclipse.jetty.websocket.api.Session session, SubscribeRequest request) {
        String authzToken = Objects.requireNonNull((String) request.params().get("_auth"));
        if (cortexInfoService.getUserInfoByAuthzToken(authzToken) == null) {
            LOGGER.error("Invalid authz token");
            sendResponse(session, BaseResponse.buildErrorResponse(
                    request.id(), JSONRPC.PROTOCOL_VERSION, ResponseErrors.INVALID_AUTH_TOKEN.toError()));
        } else {
            String sessionId = Objects.requireNonNull((String) request.params().get("session"));
            @SuppressWarnings("unchecked")
            List<String> streams = Objects.requireNonNull((List<String>) request.params().get("streams"));

            if (streams.isEmpty()) {
                LOGGER.error("No streams to subscribe");
                sendResponse(session, BaseResponse.buildErrorResponse(
                        request.id(), JSONRPC.PROTOCOL_VERSION, ResponseErrors.UNKNOWN_ERROR.toError()));
            } else {
                boolean alreadySubscribed = streams.stream()
                        .anyMatch(stream -> subscriptionService.isSubscribed(sessionId, CortexEventKind.forName(stream)));
                if (alreadySubscribed) {
                    LOGGER.error("Request to subscribe to streams {}; already subscribed to some of the streams", streams);
                    sendResponse(session, BaseResponse.buildErrorResponse(
                            request.id(), JSONRPC.PROTOCOL_VERSION, ResponseErrors.STREAM_UNAVAILABLE_OR_ALREADY_SUBSCRIBED.toError()));
                } else {
                    List<CortexEventSubscription> subscriptions = new ArrayList<>();
                    List<SubscribeResponse.StreamInfo> streamInfos = new ArrayList<>();
                    for (String stream : streams) {
                        CortexEventSubscription subscription = subscriptionService.subscribe(
                                sessionId, CortexEventKind.forName(stream));
                        streamInfos.add(subscription.getStreamInfo());
                        subscriptions.add(subscription);
                    }
                    SubscribeResponse response = new SubscribeResponse();
                    response.setId(request.id());
                    response.setJsonrpc(request.jsonrpc());
                    response.setResult(streamInfos);

                    sendResponseText(session, serializeSubscribeResponse(response));
                    subscriptions.forEach(s -> s.start(session));
                }
            }
        }
    }

    // work-around for tricky ad-hoc serialization of SubscribeResponse
    private String serializeSubscribeResponse(SubscribeResponse response) {
        ObjectNode node = new ObjectNode(mapper.getNodeFactory());
        node.set("id", new LongNode(response.id()));
        node.set("jsonrpc", new TextNode(response.jsonrpc()));

        ArrayNode resultNode = new ArrayNode(mapper.getNodeFactory());
        response.result().forEach(streamInfo -> {
            ObjectNode streamInfoNode = new ObjectNode(mapper.getNodeFactory());
            streamInfoNode.set("sid", new TextNode(streamInfo.getSubscriptionId()));
            ObjectNode streamNode = new ObjectNode(mapper.getNodeFactory());
            ArrayNode columnsNode = new ArrayNode(mapper.getNodeFactory());
            streamInfo.getColumns()
                    .forEach(columnsNode::add);
            streamNode.set("cols", columnsNode);
            streamInfoNode.set(streamInfo.getStream(), streamNode);
            resultNode.add(streamInfoNode);
        });

        node.set("result", resultNode);

        try {
            return mapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void onUnsupportedRequest(org.eclipse.jetty.websocket.api.Session session, Request request) {
        LOGGER.error("Unsupported request type: " + request.getClass());
        BaseResponse<?> errorResponse = BaseResponse.buildErrorResponse(
                request.id(), JSONRPC.PROTOCOL_VERSION, ResponseErrors.UNKNOWN_METHOD.toError());
        sendResponse(session, errorResponse);
    }

    /**
     * @return parsed request or null in case of error
     */
    private @Nullable Request parseRequest(org.eclipse.jetty.websocket.api.Session session, String message) {
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

    private void sendResponse(org.eclipse.jetty.websocket.api.Session session, Response<?> response) {
        String text;
        try {
            text = mapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize response", e);
        }

        sendResponseText(session, text);
    }

    private void sendResponseText(org.eclipse.jetty.websocket.api.Session session, String response) {
        try {
            session.getRemote().sendString(response);
        } catch (IOException e) {
            throw new RuntimeException("Failed to send response", e);
        }
    }
}
