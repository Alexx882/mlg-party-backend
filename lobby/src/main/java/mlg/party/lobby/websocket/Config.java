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

    public Config(LobbySocketHandler lobbySocketHandler) {
        this.lobbySocketHandler = lobbySocketHandler;
    }

    private final LobbySocketHandler lobbySocketHandler;

    /**
     * hosts a new endpoint for websockets at [URL]/ws and allows connections from all origins
     *
     * @param registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(lobbySocketHandler, endpointLobby).setAllowedOrigins("*");
    }
}
