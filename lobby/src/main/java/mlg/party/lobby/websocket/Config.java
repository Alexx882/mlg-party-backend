package mlg.party.lobby.websocket;

import mlg.party.games.cocktail_shaker.CocktailShakerSocketHandler;
import mlg.party.lobby.lobby.id.IIDManager;
import mlg.party.lobby.lobby.ILobbyService;
import mlg.party.lobby.logging.ILogger;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class Config implements WebSocketConfigurer {

    public Config(IRequestParser parser, ILogger logger, ILobbyService lobbyService, IIDManager iidManager) {
        this.parser = parser;
        this.logger = logger;
        this.lobbyService = lobbyService;
        this.idManager = iidManager;
    }

    private final IRequestParser parser;
    private final ILogger logger;
    private final ILobbyService lobbyService;
    private final IIDManager idManager;

    /**
     * hosts a new endpoint for websockets at [URL]/ws and allows connections from all origins
     * @param registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new LobbySocketHandler(logger, parser, lobbyService, idManager), "/ws").setAllowedOrigins("*");
        registry.addHandler(new CocktailShakerSocketHandler(), "/game/shaker").setAllowedOrigins("*");
    }
}
