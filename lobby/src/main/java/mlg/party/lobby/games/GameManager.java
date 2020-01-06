package mlg.party.lobby.games;

import mlg.party.cocktail_shaker.CocktailShakerGame;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.persistence.Basic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GameManager {

    private GameManager() {
        registerGames();
    }

    @Bean
    public static GameManager getInstance() {
        return new GameManager();
    }

    private void registerGames() {
        games.add(CocktailShakerGame.class);
    }

    private List<Class<? extends BasicGame>> games = new ArrayList<>(10);

    public Class<? extends BasicGame> getNextGame() {
         Collections.shuffle(games);
         return games.get(0);
    }
}
