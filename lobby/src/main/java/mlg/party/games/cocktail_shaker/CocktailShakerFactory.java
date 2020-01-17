package mlg.party.games.cocktail_shaker;

import mlg.party.games.GameFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.PostConstruct;

@Service
@Configuration
@EnableWebSocket
public class CocktailShakerFactory extends GameFactory<CocktailShakerGame> implements WebSocketConfigurer {

    @Value("${mlg.games.endpoints.base}")
    private String host;

    @Value("${mlg.games.endpoints.shaker}")
    private String url;

    public CocktailShakerFactory(CocktailShakerSocketHandler handler) {
        this.handler = handler;
    }

    private final CocktailShakerSocketHandler handler;

    @PostConstruct
    public void register() {
        registerFactory(this);
    }

    @Override
    public CocktailShakerGame createGame() {
        CocktailShakerGame cocktailShakerGame = new CocktailShakerGame(host+url);
        cocktailShakerGame.setSocketHandler(handler);
        return cocktailShakerGame;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, url).setAllowedOrigins("*");
    }
}
