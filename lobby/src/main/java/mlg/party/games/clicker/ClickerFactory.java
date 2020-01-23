package mlg.party.games.clicker;

import mlg.party.games.GameFactory;
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
public class ClickerFactory extends GameFactory<ClickerGame> implements WebSocketConfigurer {

    @Value("${mlg.games.endpoints.base}")
    private String host;

    @Value("${mlg.games.endpoints.clicker}")
    private String url;

    public ClickerFactory(ClickerSocketHandler handler) {
        this.handler = handler;
    }

    private final ClickerSocketHandler handler;

    public void register() {
        registerFactory(this);
    }

    @Override
    public ClickerGame createGame(String lobbyId, List<Player> players) {
        ClickerGame clickerGameGame = new ClickerGame(lobbyId, players, url);
        clickerGameGame.setSocketHandler(handler);
        return clickerGameGame;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, url).setAllowedOrigins("*");
    }
}
