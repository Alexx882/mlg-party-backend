package mlg.party.lobby.websocket;

import com.google.gson.Gson;
import mlg.party.lobby.games.BasicGame;
import mlg.party.lobby.games.GameManager;
import mlg.party.lobby.games.GameParameters;
import mlg.party.lobby.lobby.Player;
import mlg.party.lobby.lobby.id.IIDManager;
import mlg.party.lobby.lobby.ILobbyService;
import mlg.party.lobby.logging.ILogger;
import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;
import mlg.party.lobby.websocket.requests.CreateLobbyRequest;
import mlg.party.lobby.websocket.requests.JoinLobbyRequest;
import mlg.party.lobby.websocket.requests.StartGameRequest;
import mlg.party.lobby.websocket.responses.JoinLobbyResponse;
import mlg.party.lobby.websocket.responses.LobbyCreatedResponse;
import mlg.party.lobby.websocket.responses.PlayerListResponse;
import mlg.party.lobby.websocket.responses.StartGameResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component
public class SocketHandler extends TextWebSocketHandler {

    public SocketHandler(GameManager gameManager, ILogger logger, IRequestParser parser, ILobbyService lobbyService, IIDManager idManager) {
        this.gameManager = gameManager;
        this.logger = logger;
        this.parser = parser;
        this.lobbyService = lobbyService;
        this.idManager = idManager;
    }

    private final GameManager gameManager;

    private static final Gson gson = new Gson();
    private final ILogger logger;
    private final IRequestParser parser;
    private final ILobbyService lobbyService;
    private final IIDManager idManager;

    private List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private ConcurrentHashMap<WebSocketSession, Player> sessionIds = new ConcurrentHashMap<>();

    private void handle(WebSocketSession session, CreateLobbyRequest request) throws IOException {
        Player requester = sessionIds.get(session);
        requester.setName(request.getPlayerName());

        String lobbyId = lobbyService.createLobby(requester);

        LobbyCreatedResponse response = new LobbyCreatedResponse(lobbyId, requester.getId());
        session.sendMessage(new TextMessage(gson.toJson(response)));

        logger.log(this, String.format("Created new Lobby for Player(%s, %s): Lobby(%s)", requester.getId(), requester.getName(), lobbyId));
    }


    private void handle(WebSocketSession session, JoinLobbyRequest request) throws IOException {
        logger.log(this, String.format("Player(%s) wants to join Lobby(%s)", request.getPlayerName(), request.getLobbyName()));

        JoinLobbyResponse response;

        sessionIds.get(session).setName(request.getPlayerName());

        if (lobbyService.addPlayerToLobby(request.getLobbyName(), sessionIds.get(session)))
            response = new JoinLobbyResponse(200, sessionIds.get(session).getId());
        else
            response = new JoinLobbyResponse(404, sessionIds.get(session).getId());
        session.sendMessage(new TextMessage(gson.toJson(response)));

        List<Player> participants = lobbyService.getPlayersForLobby(request.getLobbyName());
        PlayerListResponse response2 = new PlayerListResponse(participants.stream().map(Player::getName).collect(Collectors.toList()));

        // todo use getPlayerWithSession
        for (Player p : participants) {
            for (WebSocketSession s : sessionIds.keySet()) {
                if (sessionIds.get(s) == p)
                    s.sendMessage(new TextMessage(gson.toJson(response2)));
            }
        }
    }

    private void handle(WebSocketSession session, StartGameRequest request) throws IOException {
        List<Player> players = null;

        // check if lobby exists
        try {
            players = lobbyService.getPlayersForLobby(request.getLobbyName());
        } catch (IllegalArgumentException e) {
            sendMessage(session, new StartGameResponse(404, ""));
            return;
        }

        BasicGame game;
        try {
            Class<? extends BasicGame> c = GameManager.getInstance().getNextGame();
            Constructor<? extends BasicGame> ctor = c.getConstructor(c);
            game = ctor.newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        game.initialize(request.getLobbyName(), getPlayersWithSession(players), this);
        game.startGame();
    }

    public void sendMessage(WebSocketSession s, Object message) throws IOException {
        s.sendMessage(new TextMessage(gson.toJson(message)));
    }

    public HashMap<Player, WebSocketSession> getPlayersWithSession(List<Player> players){
        HashMap<Player, WebSocketSession> playersWithSession = new HashMap<Player, WebSocketSession>();

        for (Player p : players) {
            for (WebSocketSession s : sessions) {
                if (sessionIds.get(s) == p)
                    playersWithSession.put(p, s);
            }
        }

        return playersWithSession;
    }

    /**
     * called whenever a new websocket message is sent to the server. the connection is already
     * established => sessions, sessionIds are already accessible
     *
     * @param session - connection the message was sent from
     * @param message - content
     * @throws Exception - IOException when connection was closed
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            handle(session, parser.parseMessage(message.getPayload()));
        } catch (IllegalArgumentException e) {
            logger.error(this, String.format("Failed to derive a type for message: %s", message.getPayload()));
        }

    }

    /**
     * chooses the correct handle(..) method based on the runtime type of request
     *
     * @param session - connection the message came from, used to send replies
     * @param request - holds the message data
     * @throws IOException - if no reply could be sent
     */
    private void handle(WebSocketSession session, BasicWebSocketRequest request) throws IOException {
        if (request instanceof JoinLobbyRequest)
            handle(session, (JoinLobbyRequest) request);
        else if (request instanceof CreateLobbyRequest)
            handle(session, (CreateLobbyRequest) request);
        else
            logger.log(this, String.format("Handling a not better specified websocketmessage with type '%s'", request.getType()));
    }

    /**
     * called when a user establishes a websocket connection to the server
     * the session gets stored in sessions and a player is created for it
     * which gets stored in sessionIds
     *
     * @param session - connection which has been newly established
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        if (!sessions.contains(session)) {
            sessions.add(session);
            sessionIds.put(session, new Player(idManager.nextId(), null));
        }
    }

    /**
     * called when a connection is closed actively (from server or user side)
     * the session gets removed from both sessions and sessionIds
     *
     * @param session - closed connection
     * @param status  - reason of closing
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        sessionIds.remove(session);
    }
}
