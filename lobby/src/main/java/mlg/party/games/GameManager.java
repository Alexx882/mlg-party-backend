package mlg.party.games;

import mlg.party.games.cocktail_shaker.CocktailShakerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GameManager {

    private List<GameFactory<?>> games = new ArrayList<>(10);

    public GameManager() {
//        games.add(cocktailShakerFactory);
    }

    public BasicGame getNextGame() {
        Collections.shuffle(games);
        return games.get(0).createGame();
    }
}
