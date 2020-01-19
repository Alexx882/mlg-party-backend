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
    private class GameWebSocketHandlerImpl extends GameWebSocketHandler<BasicGameImpl>{

        @Override
        protected RequestParserBase getMessageParser() {
            return null;
        }

        @Override
        protected ILogger getLogger() {
            return null;
        }
    }

    private class BasicGameImpl extends BasicGame<GameWebSocketHandlerImpl>{

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
    public void setup(){
        game = new BasicGameImpl("123", GameTestsHelper.getThreeArbitraryPlayers());
    }

    @Test
    public void isReadyToPlay_initial_false(){
        Assert.assertFalse(game.isReadyToPlay());
    }

    @Test
    public void identifyPlayer_nullId_false(){
        game.identifyPlayer(null, null);
    }

    @Test
    public void identifyPlayer_wrongId_false(){
        Assert.assertFalse(game.identifyPlayer("Alex", null));
    }

    @Test
    public void identifyPlayer_correctId_true(){
        Assert.assertTrue(game.identifyPlayer("1", null));
    }

    @Test
    public void identifyPlayer_sameIdTwice_true(){
        Assert.assertTrue(game.identifyPlayer("1", null));
        Assert.assertTrue(game.identifyPlayer("1", null));
    }

    @Test
    public void isReadyToPlay_sameIdThreeTimes_false(){
        Assert.assertTrue(game.identifyPlayer("1", null));
        Assert.assertTrue(game.identifyPlayer("1", null));
        Assert.assertTrue(game.identifyPlayer("1", null));

        Assert.assertFalse(game.isReadyToPlay());

    }

    @Test
    public void isReadyToPlay_correctIdThreeTimes_false(){
        Assert.assertTrue(game.identifyPlayer("1", null));
        Assert.assertTrue(game.identifyPlayer("2", null));
        Assert.assertTrue(game.identifyPlayer("3", null));

        Assert.assertTrue(game.isReadyToPlay());

    }

}
