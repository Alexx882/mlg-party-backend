package mlg.party.games.cocktail_shaker.websocket;

import mlg.party.games.cocktail_shaker.websocket.requests.CocktailShakerResult;
import mlg.party.games.websocket.requests.HelloGameRequest;
import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RequestParserTests {
    RequestParser parser;

    @Before
    public void setup() {
        parser = new RequestParser();
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseMessage_nullInput_illegalArgument() {
        parser.parseMessage(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseMessage_emptyString_illegalArgument() {
        parser.parseMessage("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseMessage_invalidJson_illegalArgument() {
        parser.parseMessage("Test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseMessage_noType_illegalArgument() {
        parser.parseMessage("{}");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseMessage_emptyType_illegalArgument() {
        parser.parseMessage("{\"type\":\"\"}");
    }

    @Test
    public void parseMessage_CocktailShakerType_cocktailShakerResult() {
        BasicWebSocketRequest req = parser.parseMessage("{\"type\":\"CocktailShakerResult\", \"playerId\":\"Alex\", \"max\":2, \"avg\":1}");
        assertIsCocktailShakerResultWithValues("Alex", 2, 1, req);
    }

    @Test
    public void parseMessage_HelloGameType_HelloGameRequest() {
        BasicWebSocketRequest req = parser.parseMessage("{\"type\":\"HelloGame\", \"playerId\":\"69\", \"lobbyName\":\"123\"}");
        assertIsHelloGameRequestWithIdAndLobby("69", "123", req);
    }

    private void assertIsCocktailShakerResultWithValues(String expectedPlayerId, float expectedMax, float expectedAvg, BasicWebSocketRequest req)
            throws AssertionError {
        Assert.assertNotNull(req);
        Assert.assertTrue(req instanceof CocktailShakerResult);

        CocktailShakerResult r = (CocktailShakerResult) req;
        Assert.assertEquals(expectedPlayerId, r.getPlayerId());
        Assert.assertEquals(expectedMax, r.getMax(), 0.001);
        Assert.assertEquals(expectedAvg, r.getAvg(), 0.001);
    }

    private void assertIsHelloGameRequestWithIdAndLobby(String expectedId, String expectedLobby, BasicWebSocketRequest req) {
        Assert.assertNotNull(req);
        Assert.assertTrue(req instanceof HelloGameRequest);

        HelloGameRequest r = (HelloGameRequest) req;
        Assert.assertEquals(expectedId, r.playerId);
        Assert.assertEquals(expectedLobby, r.lobbyName);
    }

}
