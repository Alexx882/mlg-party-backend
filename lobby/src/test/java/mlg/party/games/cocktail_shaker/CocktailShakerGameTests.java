package mlg.party.games.cocktail_shaker;

import mlg.party.games.cocktail_shaker.websocket.requests.CocktailShakerResult;
import mlg.party.lobby.lobby.Player;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CocktailShakerGameTests {
    CocktailShakerGame game;
    AtomicBoolean handled;

    @Before
    public void setup(){
        game = new CocktailShakerGame("123", getThreeArbitraryPlayers(), "/test/");

        handled = new AtomicBoolean(false);
        game.registerResultCallback((args) -> handled.set(true));
    }

    private List<Player> getThreeArbitraryPlayers() {
        var participants = new ArrayList<Player>(3);
        participants.add(new Player("1", "P1"));
        participants.add(new Player("2", "P2"));
        participants.add(new Player("3", "P3"));
        return participants;
    }

    @Test(expected = IllegalArgumentException.class)
    public void handleNewResult_null_illegalArgument(){
        game.handleNewResult(null);
    }

    @Test
    public void handleNewResult_singleResult_noCallback(){
        game.handleNewResult(new CocktailShakerResult("123", "1", 1,1));
        Assert.assertFalse(handled.get());
    }

    @Test
    public void handleNewResult_twoResults_noCallback(){
        game.handleNewResult(new CocktailShakerResult("123", "1", 1,1));
        game.handleNewResult(new CocktailShakerResult("123", "2", 1,1));
        Assert.assertFalse(handled.get());
    }

    @Test
    public void handleNewResult_threeResults_callback(){
        game.handleNewResult(new CocktailShakerResult("123", "1", 1,1));
        game.handleNewResult(new CocktailShakerResult("123", "2", 1,1));
        game.handleNewResult(new CocktailShakerResult("123", "3", 1,1));
        Assert.assertTrue(handled.get());
    }

}
