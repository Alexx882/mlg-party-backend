package mlg.party.games;

import mlg.party.Callback;
import mlg.party.games.websocket.responses.GameFinishedResponse;
import mlg.party.lobby.lobby.Player;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;

public class BasicGameTests {
    private abstract class GameWebSocketHandlerImpl extends GameWebSocketHandler<BasicGameImpl> {
    }

    /**
     * Dummy implementation of BasicGame for testing purposes.
     */
    private class BasicGameImpl extends BasicGame<BasicGameImpl, GameWebSocketHandlerImpl> {

        private BasicGameImpl(String lobbyId, List<Player> players) {
            super(lobbyId, players);
        }

        @Override
        public void startGame() {
            throw new RuntimeException("Not implemented for testing!");
        }

        @Override
        public void registerGameFinishedCallback(Callback<GameFinishedArgs> callback) {
            throw new RuntimeException("Not implemented for testing!");
        }

        @Override
        public String getGameName() {
            throw new RuntimeException("Not implemented for testing!");
        }

        @Override
        public String getGameEndpoint() {
            throw new RuntimeException("Not implemented for testing!");
        }
    }

    private BasicGameImpl game;

    @Before
    public void setup() {
        game = new BasicGameImpl("123", GameTestsHelper.getThreeArbitraryPlayers());
    }

    @Test
    public void isReadyToPlay_initial_false() {
        Assert.assertFalse(game.isReadyToPlay());
    }

    @Test(expected = IllegalArgumentException.class)
    public void identifyPlayer_nullId_illegalArgument() {
        game.identifyPlayer(null, null);
    }

    @Test
    public void identifyPlayer_wrongId_false() {
        Assert.assertFalse(game.identifyPlayer("Alex", null));
    }

    @Test
    public void identifyPlayer_correctId_true() {
        Assert.assertTrue(game.identifyPlayer("1", null));
    }

    @Test
    public void identifyPlayer_correctIdTwice_trueTrue() {
        Assert.assertTrue(game.identifyPlayer("1", null));
        Assert.assertTrue(game.identifyPlayer("2", null));
    }

    @Test
    public void identifyPlayer_sameCorrectIdTwice_trueFalse() {
        Assert.assertTrue(game.identifyPlayer("1", null));
        Assert.assertFalse(game.identifyPlayer("1", null));
    }

    @Test
    public void isReadyToPlay_sameCorrectIdThreeTimes_false() {
        game.identifyPlayer("1", null);
        game.identifyPlayer("1", null);
        game.identifyPlayer("1", null);

        Assert.assertFalse(game.isReadyToPlay());
    }

    @Test
    public void isReadyToPlay_correctIdThreeTimes_true() {
        game.identifyPlayer("1", null);
        game.identifyPlayer("2", null);
        game.identifyPlayer("3", null);

        Assert.assertTrue(game.isReadyToPlay());
    }


    @Test
    public void notifyGameFinished_sendingToSocketHandler() throws IOException {
        GameWebSocketHandlerImpl handler = Mockito.mock(GameWebSocketHandlerImpl.class);
        game.setSocketHandler(handler);

        game.notifyGameFinished(game, "1");

        Mockito.verify(handler).sendMessageToPlayers(game, new GameFinishedResponse("1"));
    }
}
