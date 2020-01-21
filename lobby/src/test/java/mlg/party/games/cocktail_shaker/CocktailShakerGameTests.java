package mlg.party.games.cocktail_shaker;

import mlg.party.games.GameTestsHelper;
import mlg.party.games.cocktail_shaker.websocket.CocktailShakerSocketHandler;
import mlg.party.games.cocktail_shaker.websocket.requests.CocktailShakerResult;
import mlg.party.games.websocket.responses.GameFinishedResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class CocktailShakerGameTests {
    CocktailShakerGame game;
    AtomicBoolean handled;

    @Before
    public void setup() {
        game = new CocktailShakerGame("123", GameTestsHelper.getThreeArbitraryPlayers(), "/test/");

        handled = new AtomicBoolean(false);
        game.registerGameFinishedCallback((args) -> handled.set(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void handleNewResult_null_illegalArgument() {
        game.handleNewResult(null);
    }

    @Test
    public void handleNewResult_singleResult_noCallback() {
        game.handleNewResult(new CocktailShakerResult("123", "1", 1, 1));
        Assert.assertFalse(handled.get());
    }

    @Test
    public void handleNewResult_twoResults_noCallback() {
        game.handleNewResult(new CocktailShakerResult("123", "1", 1, 1));
        game.handleNewResult(new CocktailShakerResult("123", "2", 1, 1));
        Assert.assertFalse(handled.get());
    }

    @Test
    public void handleNewResult_threeResults_mockedClientCallbackWithHighestAvg() throws IOException {
        CocktailShakerSocketHandler handler = Mockito.mock(CocktailShakerSocketHandler.class);
        game.setSocketHandler(handler);

        game.handleNewResult(new CocktailShakerResult("123", "1", 1, 1));
        game.handleNewResult(new CocktailShakerResult("123", "2", 3, 2));
        game.handleNewResult(new CocktailShakerResult("123", "3", 2, 3));

        Mockito.verify(handler).sendMessageToPlayers(game, new GameFinishedResponse("3"));
    }

    @Test
    public void handleNewResult_threeResults_gameFinishedCallbackWithHighestAvg() {
        CocktailShakerSocketHandler handler = Mockito.mock(CocktailShakerSocketHandler.class);
        game.setSocketHandler(handler);
        game.registerGameFinishedCallback((args) -> {
            handled.set(true);

            // highest avg wins
            Assert.assertEquals("3", args.winnerId);
        });

        game.handleNewResult(new CocktailShakerResult("123", "1", 1, 1));
        game.handleNewResult(new CocktailShakerResult("123", "2", 3, 2));
        game.handleNewResult(new CocktailShakerResult("123", "3", 2, 3));
        Assert.assertTrue(handled.get());
    }

}
