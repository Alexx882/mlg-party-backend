package mlg.party.lobby.games;

import mlg.party.cocktail_shaker.CocktailShakerGame;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameManager {
    private static GameManager instance;

    private GameManager() {
        instance.registerGames();
    }

    public static GameManager getInstance() {
        if (instance == null)
            instance = new GameManager();

        return instance;
    }

    private void registerGames() {
        games.add(CocktailShakerGame.class);
    }

    private List<Class> games = new ArrayList<Class>(10);

    public Class getNextGame() {
         Collections.shuffle(games);
         return games.get(0);
    }
}
