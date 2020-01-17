package mlg.party.games.cocktail_shaker;

import mlg.party.games.GameFactory;
import org.springframework.stereotype.Service;

@Service
public class CocktailShakerFactory implements GameFactory<CocktailShakerGame> {

    public CocktailShakerFactory() {
        System.out.println("yololo i am a cocktail ... höhöhö ");
    }

    @Override
    public CocktailShakerGame createGame() {
        return new CocktailShakerGame();
    }
}
