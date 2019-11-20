package mlg.party.lobby.websocket;

import com.google.gson.Gson;
import mlg.party.lobby.lobby.Player;
import mlg.party.lobby.lobby.id.IIDManager;
import mlg.party.lobby.lobby.ILobbyService;
import mlg.party.lobby.logging.ILogger;
import mlg.party.lobby.websocket.requests.AbstractWebsocketRequest;
import mlg.party.lobby.websocket.requests.CreateLobbyRequest;
import mlg.party.lobby.websocket.requests.JoinLobbyRequest;
import mlg.party.lobby.websocket.responses.JoinLobbyResponse;
import mlg.party.lobby.websocket.responses.LobbyCreatedResponse;
import mlg.party.lobby.websocket.responses.PlayerListResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

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
    private ConcurrentHashMap<WebSocketSession, Player> sessionIds = new ConcurrentHashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            AbstractWebsocketRequest request = parser.parseMessage(message.getPayload());

            if (request instanceof CreateLobbyRequest) {
                Player requester = sessionIds.get(session);
                requester.setName(((CreateLobbyRequest) request).getPlayerName());

                String lobbyId = lobbyService.createLobby(requester);

                LobbyCreatedResponse response = new LobbyCreatedResponse(lobbyId, requester.getId());
                session.sendMessage(new TextMessage(gson.toJson(response)));

                logger.log(this, String.format("Created new Lobby for Player(%s, %s): Lobby(%s)", requester.getId(), requester.getName(), lobbyId));
            } else if (request instanceof JoinLobbyRequest) {
                JoinLobbyRequest joinLobbyRequest = (JoinLobbyRequest) request;
                logger.log(this, String.format("Player(%s) wants to join Lobby(%s)", joinLobbyRequest.getPlayerName(), joinLobbyRequest.getLobbyName()));

                JoinLobbyResponse response;

                sessionIds.get(session).setName(joinLobbyRequest.getPlayerName());

                if (lobbyService.addPlayerToLobby(joinLobbyRequest.getLobbyName(), sessionIds.get(session)))
                    response = new JoinLobbyResponse(200, sessionIds.get(session).getId());
                else
                    response = new JoinLobbyResponse(404, sessionIds.get(session).getId());
                session.sendMessage(new TextMessage(gson.toJson(response)));

                List<Player> participants = lobbyService.getPlayersForLobby(joinLobbyRequest.getLobbyName());
                PlayerListResponse response2 = new PlayerListResponse(participants.stream().map(Player::getName).collect(Collectors.toList()));

                for (Player p : participants) {
                    for (WebSocketSession s : sessionIds.keySet()) {
                        if (sessionIds.get(s) == p)
                            session.sendMessage(new TextMessage(gson.toJson(response2)));
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            logger.error(this, String.format("Failed to derive a type for message: %s", message.getPayload()));
        }

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if (!sessions.contains(session)) {
            sessions.add(session);
            sessionIds.put(session, new Player(idManager.nextId(), null));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        sessionIds.remove(session);
    }
}
