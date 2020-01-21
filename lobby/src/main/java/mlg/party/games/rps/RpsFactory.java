package mlg.party.games.rps;

import mlg.party.games.GameFactory;
import mlg.party.games.cocktail_shaker.CocktailShakerGame;
import mlg.party.games.cocktail_shaker.CocktailShakerSocketHandler;
import mlg.party.lobby.lobby.Player;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Configuration
@EnableWebSocket
public class RpsFactory extends GameFactory<RpsGame> implements WebSocketConfigurer {

    @Value("${mlg.games.endpoints.base}")
    private String host;

    @Value("${mlg.games.endpoints.rps}")
    private String url;

    public RpsFactory(RpsSocketHandler handler) {
        this.handler = handler;
    }

    private final RpsSocketHandler handler;

    @PostConstruct
    public void register() {
        registerFactory(this);
    }

    @Override
    public RpsGame createGame(String lobbyId, List<Player> players) {
        RpsGame rpsGame = new RpsGame(lobbyId, players, url);
        rpsGame.setSocketHandler(handler);
        return rpsGame;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, url).setAllowedOrigins("*");
    }
}
