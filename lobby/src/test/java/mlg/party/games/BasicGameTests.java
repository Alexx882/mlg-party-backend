package mlg.party.games;

import mlg.party.Callback;
import mlg.party.RequestParserBase;
import mlg.party.lobby.lobby.Player;
import mlg.party.lobby.logging.ILogger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class BasicGameTests {
    private abstract class GameWebSocketHandlerImpl extends GameWebSocketHandler<BasicGameImpl> { }

    private class BasicGameImpl extends BasicGame<GameWebSocketHandlerImpl> {

        public BasicGameImpl(String lobbyId, List<Player> players) {
            super(lobbyId, players);
        }

        @Override
        public void startGame() {

        }

        @Override
        public void registerResultCallback(Callback<GameFinishedArgs> callback) {

        }

        @Override
        public String getGameName() {
            return null;
        }

        @Override
        public String getGameEndpoint() {
            return null;
        }
    }

    BasicGame game;

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

}
