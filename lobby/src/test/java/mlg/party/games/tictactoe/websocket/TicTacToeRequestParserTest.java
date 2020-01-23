package mlg.party.games.tictactoe.websocket;


import mlg.party.games.websocket.requests.HelloGameRequest;
import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import  mlg.party.games.tictactoe.websocket.requests.TicTacToeMoveRequest;

public class TicTacToeRequestParserTest {

    TicTacToeRequestParser parser;


    @Before
    public void setup() {
        parser = new TicTacToeRequestParser();
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
    public void parseMessage_TicTacToeMoveRequest() {
        BasicWebSocketRequest req = parser.parseMessage("{\"playerId\":\"Alex\",\"lobbyId\":\"1234\", \"x\":2, \"y\":1,\"type\":\"TicTacToeMove\"}");
        assertIsTicTacToeMoveRequestWithValues(req, "Alex","1234", 2, 1);
    }

    private void assertIsTicTacToeMoveRequestWithValues(BasicWebSocketRequest req, String playerId,String lobbyId, int x, int y)
            throws AssertionError {
        Assert.assertNotNull(req);
        Assert.assertTrue(req instanceof TicTacToeMoveRequest);

        TicTacToeMoveRequest r = (TicTacToeMoveRequest) req;
        Assert.assertEquals(playerId, r.playerId);
        Assert.assertEquals(lobbyId,r.lobbyId);
        Assert.assertEquals(x, r.x);
        Assert.assertEquals(y, r.y);
    }

    @Test
    public void parseMessage_StartGaneRequest() {
        BasicWebSocketRequest req = parser.parseMessage("{\"playerId\":\"Alex\",\"lobbyId\":\"asdf\", \"type\":\"HelloGame\"}");
        assertIsStartGameRequestWithValues(req, "Alex", "asdf");
    }

    private void assertIsStartGameRequestWithValues(BasicWebSocketRequest req, String playerId,String lobbyName)
            throws AssertionError {
        Assert.assertNotNull(req);
        Assert.assertTrue(req instanceof HelloGameRequest);
        HelloGameRequest r = (HelloGameRequest) req;
        Assert.assertEquals(playerId, r.playerId);
        Assert.assertEquals(lobbyName,r.lobbyId);
    }
}
