package mlg.party.lobby.websocket;

import mlg.party.games.GameManager;
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

    public Config(GameManager gameManager, IRequestParser parser, ILogger logger, ILobbyService lobbyService, IIDManager iidManager) {
        this.gameManager = gameManager;
        this.parser = parser;
        this.logger = logger;
        this.lobbyService = lobbyService;
        this.idManager = iidManager;
    }

    private final GameManager gameManager;
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
        registry.addHandler(new SocketHandler(logger, parser, lobbyService, idManager, gameManager), "/ws").setAllowedOrigins("*");
    }
}
