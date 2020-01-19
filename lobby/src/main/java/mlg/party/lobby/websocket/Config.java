package mlg.party.lobby.websocket;

import mlg.party.RequestParserBase;
import mlg.party.lobby.lobby.id.IIDManager;
import mlg.party.lobby.lobby.ILobbyService;
import mlg.party.lobby.logging.ILogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class Config implements WebSocketConfigurer {

    @Value("${mlg.games.endpoints.lobby}")
    private String endpointLobby;

    public Config(RequestParserBase parser, ILogger logger, ILobbyService lobbyService, IIDManager iidManager) {
        this.parser = parser;
        this.logger = logger;
        this.lobbyService = lobbyService;
        this.idManager = iidManager;
    }

    private final RequestParserBase parser;
    private final ILogger logger;
    private final ILobbyService lobbyService;
    private final IIDManager idManager;

    /**
     * hosts a new endpoint for websockets at [URL]/ws and allows connections from all origins
     * @param registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new LobbySocketHandler(logger, parser, lobbyService, idManager), endpointLobby).setAllowedOrigins("*");
    }
}
