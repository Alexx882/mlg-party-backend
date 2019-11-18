package mlg.party.lobby.websocket;

import com.google.gson.Gson;
import mlg.party.lobby.lobby.IIDManager;
import mlg.party.lobby.lobby.ILobbyService;
import mlg.party.lobby.logging.ILogger;
import mlg.party.lobby.websocket.requests.AbstractWebsocketRequest;
import mlg.party.lobby.websocket.requests.CreateLobbyRequest;
import mlg.party.lobby.websocket.responses.LobbyCreatedResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SocketHandler extends TextWebSocketHandler {

    public SocketHandler(ILogger logger, IRequestParser parser, ILobbyService lobbyService, IIDManager idManager) {
        this.logger = logger;
        this.parser = parser;
        this.lobbyService = lobbyService;
        this.idManager = idManager;
    }

    private static final Gson gson = new Gson();
    private final ILogger logger;
    private final IRequestParser parser;
    private final ILobbyService lobbyService;
    private final IIDManager idManager;

    private List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private ConcurrentHashMap<WebSocketSession, String> sessionIds = new ConcurrentHashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.log(this, "new websocket message: " + message.getPayload());

        try {
            AbstractWebsocketRequest request = parser.parseMessage(message.getPayload());

            if (request instanceof CreateLobbyRequest) {
                String lobbyId = lobbyService.createLobby(sessionIds.get(session));

                LobbyCreatedResponse response = new LobbyCreatedResponse(lobbyId, sessionIds.get(session));
                session.sendMessage(new TextMessage(gson.toJson(response)));

                logger.log(this, String.format("Created new Lobby for Player(%s): Lobby(%s)", sessionIds.get(session), lobbyId));
            }
        } catch (IllegalArgumentException e) {
            logger.error(this, "Failed to derive a type for the given message.");
        }

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if (!sessions.contains(session)) {
            sessions.add(session);
            sessionIds.put(session, idManager.nextId());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        sessionIds.remove(session);
    }
}
