package mlg.party.games.cocktail_shaker.websocket;

import mlg.party.games.cocktail_shaker.websocket.requests.CocktailShakerResult;
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
        assertIsCocktailShakerResultWithValues(req, "Alex", 2, 1);
    }

    private void assertIsCocktailShakerResultWithValues(BasicWebSocketRequest req, String playerId, float max, float avg)
            throws AssertionError {
        Assert.assertNotNull(req);
        Assert.assertTrue(req instanceof CocktailShakerResult);

        CocktailShakerResult r = (CocktailShakerResult) req;
        Assert.assertEquals(playerId, r.playerId);
        Assert.assertEquals(max, r.max, 0.001);
        Assert.assertEquals(avg, r.avg, 0.001);
    }

}
