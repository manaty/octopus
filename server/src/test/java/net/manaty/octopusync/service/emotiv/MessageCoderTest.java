package net.manaty.octopusync.service.emotiv;

import net.manaty.octopusync.service.emotiv.message.*;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

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
