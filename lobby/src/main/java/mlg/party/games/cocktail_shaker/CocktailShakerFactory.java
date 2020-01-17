package mlg.party.games.cocktail_shaker;

import mlg.party.games.GameFactory;
import mlg.party.lobby.logging.ILogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class CocktailShakerFactory extends GameFactory<CocktailShakerGame> {

    @Value("${mlg.games.endpoints.shaker}")
    private String url;

    public CocktailShakerFactory(CocktailShakerSocketHandler handler) {
        this.handler = handler;
    }

    private final CocktailShakerSocketHandler handler;

    @PostConstruct
    public void register() {
        registerFactory(this);
        System.out.println("urlurlurl: " +url);
    }

    @Override
    public CocktailShakerGame createGame() {
        CocktailShakerGame cocktailShakerGame = new CocktailShakerGame(url);
        cocktailShakerGame.setSocketHandler(handler);
        return cocktailShakerGame;
    }
}
