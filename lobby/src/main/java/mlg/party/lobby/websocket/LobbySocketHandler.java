package mlg.party.lobby.websocket;

import com.google.gson.Gson;
import mlg.party.RequestParserBase;
import mlg.party.games.BasicGame;
import mlg.party.games.GameFactory;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component
public class LobbySocketHandler extends TextWebSocketHandler {

    public LobbySocketHandler(ILogger logger, RequestParserBase parser, ILobbyService lobbyService, IIDManager idManager) {
        this.logger = logger;
        this.parser = parser;
        this.lobbyService = lobbyService;
        this.idManager = idManager;
    }

    private static final Gson gson = new Gson();
    private final ILogger logger;
    private final RequestParserBase parser;
    private final ILobbyService lobbyService;
    private final IIDManager idManager;

    private List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private ConcurrentHashMap<WebSocketSession, Player> sessionIds = new ConcurrentHashMap<>();

    /**
     * called when a new lobby got created
     *
     * @param session - the requesters websocket connection
     * @param request - contains the lobby ID and playername
     * @throws IOException - unexpectedly closing connection, etc.
     */
    private void handle(WebSocketSession session, CreateLobbyRequest request) throws IOException {
        Player requester = sessionIds.get(session);
        requester.setName(request.getPlayerName());

        String lobbyId = lobbyService.createLobby(requester);

        LobbyCreatedResponse response = new LobbyCreatedResponse(lobbyId, requester.getId());
        session.sendMessage(new TextMessage(gson.toJson(response)));

        logger.log(this, String.format("Created new Lobby for Player(%s, %s): Lobby(%s)", requester.getId(), requester.getName(), lobbyId));
    }

    /**
     * callend when a player wants to join an existing lobby
     *
     * @param session - the requesters websocket connection
     * @param request - contains the lobby ID and playername
     * @throws IOException - unexpectedly closing connection, etc.
     */
    private void handle(WebSocketSession session, JoinLobbyRequest request) throws IOException {
        logger.log(this, String.format("Player(%s) wants to join Lobby(%s)", request.getPlayerName(), request.getLobbyId()));

        JoinLobbyResponse response;

        sessionIds.get(session).setName(request.getPlayerName());

        if (lobbyService.addPlayerToLobby(request.getLobbyId(), sessionIds.get(session)))
            response = new JoinLobbyResponse(200, sessionIds.get(session).getId());
        else
            response = new JoinLobbyResponse(404, sessionIds.get(session).getId());
        session.sendMessage(new TextMessage(gson.toJson(response)));

        List<Player> participants = lobbyService.getPlayersForLobby(request.getLobbyId());
        PlayerListResponse response2 = new PlayerListResponse(participants.stream().map(Player::getName).collect(Collectors.toList()));

        sendMessageToPlayers(
                participants.stream().filter(
                        (p) -> !p.getName().equals(request.getPlayerName())
                ).collect(Collectors.toList()),
                gson.toJson(response2)
        );
    }

    /**
     * sends a message to a selection of players
     *
     * @param players - recievers of the message
     * @param message - string to be sent
     * @throws IOException - unecpected closing of the WebSocket, no connection, etc
     */
    private void sendMessageToPlayers(List<Player> players, String message) throws IOException {
        // fixme occasionally causes a ConcurrentModificationException
        for (Player p : players) {
            for (WebSocketSession s : sessionIds.keySet()) {
                if (sessionIds.get(s) == p)
                    s.sendMessage(new TextMessage(message));
            }
        }
    }

    /**
     * called when a new game starts.
     * 1. Creates a new game instance from the GameFactory
     * 2. Registers the game at its socket Handler
     * 3. Informs the players in the same lobby as the requester about the new game
     * 4. Deletes lobby information from this handler as the game's SocketHandler is in charge of the lobby
     *
     * @param session - connection of the requester
     * @param request - request to handle
     * @throws IOException - unexpected closing of a session of one of the participants
     */
    private void handle(WebSocketSession session, StartGameRequest request) throws IOException {
        List<Player> players ;
        try {
            players = lobbyService.getPlayersForLobby(request.getLobbyId());
        } catch (IllegalArgumentException e) {
            // if lobby does not exist
            sendMessage(session, new StartGameResponse(404, ""));
            return;
        }

        logger.log(this, String.format("lobby '%s' wants to start the game.", request.getLobbyId()));

        // 1. select a new random game from the register
        BasicGame<?, ?> game = GameFactory.getRandomGameFactory().createGame(request.getLobbyId(), lobbyService.getPlayersForLobby(request.getLobbyId()));

        // 2. give the game information about participating players and their websocket
        game.startGame();

        // 3. inform the players about the new game
        StartGameResponse response = new StartGameResponse(200, game.getGameEndpoint());
        sendMessageToPlayers(lobbyService.getPlayersForLobby(request.getLobbyId()), gson.toJson(response));

        // 4. clear lobby info as the lobby is now "owned" by the GameSocketHandler
        lobbyService.closeLobby(request.getLobbyId());

        // 5. close websocket connections and remove them from the lists
        List<WebSocketSession> toRemove = new LinkedList<>();
        for (Player p : players) {
            for (WebSocketSession s : sessionIds.keySet()) {
                if (sessionIds.get(s) == p) {
                    s.close(CloseStatus.NORMAL);
                    toRemove.add(s);
                }
            }
        }
        for (WebSocketSession s : toRemove) {
            sessions.remove(s);
            sessionIds.remove(s);
        }
    }

    /**
     * called when a game has finished. it uses the existing connection to ANOTHER endpoint to tell the
     * players what the next game is and where to connect
     *
     * @param lobbyId           - unique identifier for lobby
     * @param playerConnections - maps participant to their websocket connection
     */
    public void redirectToNewGame(String lobbyId, Map<Player, WebSocketSession> playerConnections) throws IOException {
        //SOME DELAY
        try{
            Thread.sleep(1500);
        }catch(InterruptedException e){
            logger.error("SLEEP","sleeping failed epically");
        }
        List<Player> participants = new LinkedList<>(playerConnections.keySet());

        // 1. select a new random game from the register
        BasicGame<?, ?> game = GameFactory.getRandomGameFactory().createGame(lobbyId, participants);

        // 2. give the game information about participating players and their websocket
        game.startGame();

        // 3. inform the players about the new game
        StartGameResponse response = new StartGameResponse(200, game.getGameEndpoint());
        for (WebSocketSession connection : playerConnections.values())
            connection.sendMessage(new TextMessage(gson.toJson(response)));

        // 4. close the websockets
        for (WebSocketSession session : playerConnections.values())
            session.close(CloseStatus.NORMAL);

    }

    public void sendMessage(WebSocketSession s, Object message) throws IOException {
        s.sendMessage(new TextMessage(gson.toJson(message)));
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
        else if (request instanceof StartGameRequest)
            handle(session, (StartGameRequest) request);
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
