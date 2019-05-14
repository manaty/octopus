package net.manaty.octopusync.service.emotiv;

import net.manaty.octopusync.service.emotiv.json.MessageCoder;
import net.manaty.octopusync.service.emotiv.message.*;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MessageCoderTest {

    private final MessageCoder coder = new MessageCoder();

    @Test
    public void testSerialization_GetUserLoginRequest() throws JSONException {
        GetUserLoginRequest request = new GetUserLoginRequest(1);
        String encoded = coder.encodeRequest(request);
        String expected = "{" +
                "  \"jsonrpc\": \"2.0\"," +
                "  \"method\": \"getUserLogin\"," +
                "  \"id\": 1" +
                "}";
        JSONAssert.assertEquals(expected, encoded, false);
    }

    @Test
    public void testDeserialization_GetUserLoginResponse() throws Exception {
        GetUserLoginResponse expected = new GetUserLoginResponse();
        expected.setId(1);
        expected.setJsonrpc("2.0");
        expected.setResult(Arrays.asList("username1", "username2"));

        String json = "{" +
                "  \"jsonrpc\": \"2.0\"," +
                "  \"id\":1," +
                "  \"result\": [\"username1\", \"username2\"]" +
                "}";
        GetUserLoginResponse decoded = coder.decodeResponse(GetUserLoginResponse.class, json);
        assertEquals(expected.jsonrpc(), decoded.jsonrpc());
        assertEquals(expected.id(), decoded.id());
        assertEquals(expected.error(), decoded.error());
        assertEquals(expected.result(), decoded.result());
    }

    @Test
    public void testSerialization_LoginRequest() throws JSONException {
        LoginRequest request = new LoginRequest(
                1, "username", "password", "clientId", "clientSecret");
        String encoded = coder.encodeRequest(request);
        String expected = "{" +
                "  \"jsonrpc\": \"2.0\"," +
                "  \"method\": \"login\"," +
                "  \"params\": {" +
                "    \"username\": \"username\"," +
                "    \"password\": \"password\"," +
                "    \"client_id\": \"clientId\"," +
                "    \"client_secret\": \"clientSecret\"" +
                "  }," +
                "  \"id\": 1" +
                "}";
        JSONAssert.assertEquals(expected, encoded, false);
    }

    @Test
    public void testDeserialization_LoginResponse() throws Exception {
        LoginResponse expected = new LoginResponse();
        expected.setId(1);
        expected.setJsonrpc("2.0");

        String json = "{" +
                "  \"jsonrpc\": \"2.0\"," +
                "  \"id\":1," +
                "  \"result\": \"...\"" +
                "}";
        
        LoginResponse decoded = coder.decodeResponse(LoginResponse.class, json);
        assertEquals(expected.jsonrpc(), decoded.jsonrpc());
        assertEquals(expected.id(), decoded.id());
        assertEquals(expected.error(), decoded.error());
    }

    @Test
    public void testSerialization_LogoutRequest() throws JSONException {
        LogoutRequest request = new LogoutRequest(1, "username");
        String encoded = coder.encodeRequest(request);
        String expected = "{" +
                "    \"jsonrpc\": \"2.0\"," +
                "    \"method\": \"logout\"," +
                "    \"params\": {" +
                "      \"username\": \"username\"" +
                "    }," +
                "    \"id\": 1" +
                "  }";
        JSONAssert.assertEquals(expected, encoded, false);
    }

    @Test
    public void testDeserialization_LogoutResponse() throws Exception {
        LogoutResponse expected = new LogoutResponse();
        expected.setId(1);
        expected.setJsonrpc("2.0");

        String json = "{" +
                "  \"jsonrpc\": \"2.0\"," +
                "  \"id\":1," +
                "  \"result\": \"...\"" +
                "}";

        LogoutResponse decoded = coder.decodeResponse(LogoutResponse.class, json);
        assertEquals(expected.jsonrpc(), decoded.jsonrpc());
        assertEquals(expected.id(), decoded.id());
        assertEquals(expected.error(), decoded.error());
    }

    @Test
    public void testSerialization_AuthorizeRequest() throws JSONException {
        AuthorizeRequest request = new AuthorizeRequest(
                1, "clientId", "clientSecret", "license", 3);
        String encoded = coder.encodeRequest(request);
        String expected = "{" +
                "  \"jsonrpc\": \"2.0\"," +
                "  \"method\": \"authorize\"," +
                "  \"params\": {" +
                "    \"client_id\": \"clientId\"," +
                "    \"client_secret\": \"clientSecret\"," +
                "    \"license\": \"license\"," +
                "    \"debit\": 3" +
                "  }," +
                "  \"id\": 1" +
                "}";
        JSONAssert.assertEquals(expected, encoded, false);
    }

    @Test
    public void testDeserialization_AuthorizeResponse() throws Exception {
        AuthorizeResponse expected = new AuthorizeResponse();
        expected.setId(1);
        expected.setJsonrpc("2.0");
        AuthorizeResponse.AuthTokenHolder holder = new AuthorizeResponse.AuthTokenHolder();
        holder.setToken("authzToken");
        expected.setResult(holder);

        String json = "{" +
                "  \"jsonrpc\": \"2.0\"," +
                "  \"id\":1," +
                "  \"result\": {" +
                "    \"_auth\": \"authzToken\"" +
                "  }" +
                "}";

        AuthorizeResponse decoded = coder.decodeResponse(AuthorizeResponse.class, json);
        assertEquals(expected.jsonrpc(), decoded.jsonrpc());
        assertEquals(expected.id(), decoded.id());
        assertEquals(expected.error(), decoded.error());
        assertEquals(expected.result().getToken(), decoded.result().getToken());
    }

    @Test
    public void testSerialization_QueryHeadsetsRequest() throws JSONException {
        QueryHeadsetsRequest request = new QueryHeadsetsRequest(1);
        String encoded = coder.encodeRequest(request);
        String expected = "{" +
                "    \"jsonrpc\": \"2.0\"," +
                "    \"method\": \"queryHeadsets\"," +
                "    \"params\": {}," +
                "    \"id\": 1" +
                "  }";
        JSONAssert.assertEquals(expected, encoded, false);
    }

    @Test
    public void testDeserialization_QueryHeadsetsResponse() throws Exception {
        QueryHeadsetsResponse expected = new QueryHeadsetsResponse();
        expected.setId(1);
        expected.setJsonrpc("2.0");
        Headset headset = new Headset();
        headset.setId("id");
        expected.setResult(Collections.singletonList(headset));

        String json = "{" +
                "  \"jsonrpc\": \"2.0\"," +
                "  \"id\": 1," +
                "  \"result\": [" +
                "    {" +
                "      \"id\": \"id\"" +
                "    }" +
                "  ]" +
                "}";

        QueryHeadsetsResponse decoded = coder.decodeResponse(QueryHeadsetsResponse.class, json);
        assertEquals(expected.jsonrpc(), decoded.jsonrpc());
        assertEquals(expected.id(), decoded.id());
        assertEquals(expected.error(), decoded.error());
        assertEquals(expected.result().size(), decoded.result().size());
        assertHeadsetEquals(expected.result().get(0), decoded.result().get(0));
    }

    private void assertHeadsetEquals(Headset expected, Headset actual) {
        assertEquals(expected.getId(), actual.getId());
    }

    @Test
    public void testSerialization_QuerySessionsRequest() throws JSONException {
        QuerySessionsRequest request = new QuerySessionsRequest(1, "authzToken", "octopus");
        String encoded = coder.encodeRequest(request);
        String expected = "{" +
                "  \"jsonrpc\": \"2.0\"," +
                "  \"method\": \"querySessions\"," +
                "  \"params\": {" +
                "    \"_auth\": \"authzToken\"," +
                "    \"query\": {" +
                "      \"appId\": \"octopus\"" +
                "    }" +
                "  }," +
                "  \"id\": 1" +
                "}";
        JSONAssert.assertEquals(expected, encoded, false);
    }

    @Test
    public void testDeserialization_QuerySessionsResponse() throws Exception {
        ZonedDateTime started = ZonedDateTime.now().minusHours(1);

        QuerySessionsResponse expected = new QuerySessionsResponse();
        expected.setId(1);
        expected.setJsonrpc("2.0");
        Session session = new Session();
        session.setAppId("appId");
        session.setId("id");
        session.setLicense("license");
        session.setOwner("owner");
        session.setStatus("status");
        session.setStarted(started.toLocalDateTime());
        Headset headset = new Headset();
        headset.setId("id");
        session.setHeadset(headset);
        expected.setResult(Collections.singletonList(session));

        String json = "{" +
                "  \"jsonrpc\": \"2.0\"," +
                "  \"id\": 1," +
                "  \"result\": [" +
                "    {" +
                "      \"appId\": \"appId\"," +
                "      \"id\": \"id\"," +
                "      \"license\": \"license\"," +
                "      \"owner\": \"owner\"," +
                "      \"status\": \"status\"," +
                "      \"started\": \""+ DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(started)+"\"," +
                "      \"headset\": {" +
                "         \"id\": \"id\"" +
                "      }" +
                "    }" +
                "  ]" +
                "}";

        QuerySessionsResponse decoded = coder.decodeResponse(QuerySessionsResponse.class, json);
        assertEquals(expected.jsonrpc(), decoded.jsonrpc());
        assertEquals(expected.id(), decoded.id());
        assertEquals(expected.error(), decoded.error());
        assertEquals(expected.result().size(), decoded.result().size());
        assertSessionEquals(expected.result().get(0), decoded.result().get(0));
    }

    @Test
    public void testSerialization_CreateSessionRequest() throws JSONException {
        CreateSessionRequest request = new CreateSessionRequest(
                1, "authzToken", "headset", "status");
        String encoded = coder.encodeRequest(request);
        String expected = "{" +
                "    \"jsonrpc\": \"2.0\"," +
                "    \"method\": \"createSession\"," +
                "    \"params\": {" +
                "      \"_auth\": \"authzToken\"," +
                "      \"headset\": \"headset\"," +
                "      \"status\": \"status\"" +
                "    }," +
                "    \"id\": 1" +
                "}";
        JSONAssert.assertEquals(expected, encoded, false);
    }

    @Test
    public void testDeserialization_CreateSessionResponse() throws Exception {
        ZonedDateTime started = ZonedDateTime.now().minusHours(1);

        CreateSessionResponse expected = new CreateSessionResponse();
        expected.setId(1);
        expected.setJsonrpc("2.0");
        Session session = new Session();
        session.setAppId("appId");
        session.setId("id");
        session.setLicense("license");
        session.setOwner("owner");
        session.setStatus("status");
        session.setStarted(started.toLocalDateTime());
        Headset headset = new Headset();
        headset.setId("id");
        session.setHeadset(headset);
        expected.setResult(session);

        String json = "{" +
                "  \"jsonrpc\": \"2.0\"," +
                "  \"id\": 1," +
                "  \"result\": {" +
                "      \"appId\": \"appId\"," +
                "      \"id\": \"id\"," +
                "      \"license\": \"license\"," +
                "      \"owner\": \"owner\"," +
                "      \"status\": \"status\"," +
                "      \"started\": \""+ DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(started)+"\"," +
                "      \"headset\": {" +
                "         \"id\": \"id\"" +
                "      }" +
                "  }" +
                "}";

        CreateSessionResponse decoded = coder.decodeResponse(CreateSessionResponse.class, json);
        assertEquals(expected.jsonrpc(), decoded.jsonrpc());
        assertEquals(expected.id(), decoded.id());
        assertEquals(expected.error(), decoded.error());
        assertSessionEquals(expected.result(), decoded.result());
    }

    @Test
    public void testSerialization_UpdateSessionRequest() throws JSONException {
        UpdateSessionRequest request = new UpdateSessionRequest(
                1, "authzToken", "session", "status");
        String encoded = coder.encodeRequest(request);
        String expected = "{" +
                "  \"jsonrpc\": \"2.0\"," +
                "  \"method\": \"updateSession\"," +
                "  \"params\": {" +
                "    \"_auth\": \"authzToken\"," +
                "    \"session\": \"session\"," +
                "    \"status\": \"status\"" +
                "  }," +
                "  \"id\": 1" +
                "}";
        JSONAssert.assertEquals(expected, encoded, false);
    }

    @Test
    public void testDeserialization_UpdateSessionResponse() throws Exception {
        ZonedDateTime started = ZonedDateTime.now().minusHours(1);

        UpdateSessionResponse expected = new UpdateSessionResponse();
        expected.setId(1);
        expected.setJsonrpc("2.0");
        Session session = new Session();
        session.setAppId("appId");
        session.setId("id");
        session.setLicense("license");
        session.setOwner("owner");
        session.setStatus("status");
        session.setStarted(started.toLocalDateTime());
        Headset headset = new Headset();
        headset.setId("id");
        session.setHeadset(headset);
        expected.setResult(session);

        String json = "{" +
                "  \"jsonrpc\": \"2.0\"," +
                "  \"id\": 1," +
                "  \"result\": {" +
                "      \"appId\": \"appId\"," +
                "      \"id\": \"id\"," +
                "      \"license\": \"license\"," +
                "      \"owner\": \"owner\"," +
                "      \"status\": \"status\"," +
                "      \"started\": \""+ DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(started)+"\"," +
                "      \"headset\": {" +
                "         \"id\": \"id\"" +
                "      }" +
                "  }" +
                "}";

        UpdateSessionResponse decoded = coder.decodeResponse(UpdateSessionResponse.class, json);
        assertEquals(expected.jsonrpc(), decoded.jsonrpc());
        assertEquals(expected.id(), decoded.id());
        assertEquals(expected.error(), decoded.error());
        assertSessionEquals(expected.result(), decoded.result());
    }

    private static void assertSessionEquals(Session expected, Session actual) {
        assertEquals(expected.getAppId(), actual.getAppId());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getLicense(), actual.getLicense());
        assertEquals(expected.getOwner(), actual.getOwner());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getStarted(), actual.getStarted());

        assertNotNull(expected.getHeadset());
        assertNotNull(actual.getHeadset());
        assertEquals(expected.getHeadset().getId(), actual.getHeadset().getId());
    }

    @Test
    public void testSerialization_SubscribeRequest() throws JSONException {
        SubscribeRequest request = new SubscribeRequest(
                1, "authzToken", new HashSet<>(Arrays.asList("stream1", "stream2")), "sessionId");
        String encoded = coder.encodeRequest(request);
        String expected = "{" +
                "  \"jsonrpc\": \"2.0\"," +
                "  \"method\": \"subscribe\"," +
                "  \"params\": {" +
                "    \"_auth\": \"authzToken\"," +
                "    \"streams\": [" +
                "      \"stream1\", " +
                "      \"stream2\"" +
                "    ]," +
                "    \"session\": \"sessionId\"," +
                "    \"replay\": false" +
                "  }," +
                "  \"id\": 1" +
                "}";
        JSONAssert.assertEquals(expected, encoded, false);
    }

    @Test
    public void testDeserialization_SubscribeResponse() throws Exception {
        SubscribeResponse expected = new SubscribeResponse();
        expected.setId(1);
        expected.setJsonrpc("2.0");
        SubscribeResponse.StreamInfo streamInfo = new SubscribeResponse.StreamInfo();
        streamInfo.setStream("stream1");
        streamInfo.setSubscriptionId("subscriptionId");
        streamInfo.setColumns(Arrays.asList("col1", "col2", "col3"));
        expected.setResult(Collections.singletonList(streamInfo));

        String json = "{" +
                "  \"id\": 1," +
                "  \"jsonrpc\": \"2.0\"," +
                "  \"result\": [" +
                "    {" +
                "      \"stream1\": {" +
                "        \"cols\": [" +
                "          \"col1\"," +
                "          \"col2\"," +
                "          \"col3\"" +
                "        ]" +
                "      }," +
                "      \"sid\": \"subscriptionId\"" +
                "    }" +
                "  ]" +
                "}";

        SubscribeResponse decoded = coder.decodeResponse(SubscribeResponse.class, json);
        assertEquals(expected.jsonrpc(), decoded.jsonrpc());
        assertEquals(expected.id(), decoded.id());
        assertEquals(expected.error(), decoded.error());
        assertEquals(expected.result().size(), decoded.result().size());
        assertEquals(expected.result().get(0).getStream(), decoded.result().get(0).getStream());
        assertEquals(expected.result().get(0).getColumns(), decoded.result().get(0).getColumns());
        assertEquals(expected.result().get(0).getSubscriptionId(), decoded.result().get(0).getSubscriptionId());
    }

    @Test(expected = Exception.class)
    public void testDeserialization_SubscribeResponse_MoreThanOneStream() throws Exception {
        String json = "{" +
                "  \"id\": 1," +
                "  \"jsonrpc\": \"2.0\"," +
                "  \"result\": [" +
                "    {" +
                "      \"stream1\": {" +
                "        \"cols\": [" +
                "          \"col1\"," +
                "          \"col2\"," +
                "          \"col3\"" +
                "        ]" +
                "      }," +
                "      \"stream2\": {}," +
                "      \"sid\": \"subscriptionId\"" +
                "    }" +
                "  ]" +
                "}";

        coder.decodeResponse(SubscribeResponse.class, json);
    }
}
