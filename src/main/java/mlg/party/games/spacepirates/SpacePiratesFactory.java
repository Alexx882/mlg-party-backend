package mlg.party.games.spacepirates;

import mlg.party.games.GameFactory;
import mlg.party.games.spacepirates.websocket.SpacePiratesSocketHandler;
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
public class SpacePiratesFactory extends GameFactory<SpacePiratesGame> implements WebSocketConfigurer {

    @Value("${mlg.games.endpoints.base}")
    private String host;

    @Value("${mlg.games.endpoints.spacepirates}")
    private String url;

    public SpacePiratesFactory(SpacePiratesSocketHandler handler) {
        this.handler = handler;
    }

    private final SpacePiratesSocketHandler handler;

    public void register() {
        registerFactory(this);
    }

    @Override
    public SpacePiratesGame createGame(String lobbyId, List<Player> players) {
        SpacePiratesGame spacePiratesGame = new SpacePiratesGame(lobbyId, players, url);
        spacePiratesGame.setSocketHandler(handler);
        return spacePiratesGame;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, url).setAllowedOrigins("*");
    }
}
