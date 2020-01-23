package mlg.party.games;

import com.google.gson.Gson;
import mlg.party.games.websocket.requests.HelloGameRequest;
import mlg.party.games.websocket.responses.HelloGameResponse;
import mlg.party.lobby.lobby.Player;
import mlg.party.lobby.logging.ILogger;
import mlg.party.RequestParserBase;
import mlg.party.lobby.websocket.LobbySocketHandler;
import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles websocket messages for generic game instances.
 * One handler instance manages all game instances for one game type.
 *
 * @param <T> The type of game to be managed.
 */
public abstract class GameWebSocketHandler<T extends BasicGame<?, ?>> extends TextWebSocketHandler {
    protected Map<String, T> gameInstances = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    /**
     * Registers a new game in the handler where actions can be handled
     *
     * @param instance - instance of the game
     */
    public void registerNewGameInstance(T instance) {
        gameInstances.put(instance.lobbyId, instance);
        getLogger().log(this, String.format("Registered Game(%s) for Players: %s", instance.getGameName(), instance.players));
    }

    public void removeGameInstance(T instance) {
        gameInstances.remove(instance.lobbyId);
    }

    public void sendMessageToPlayers(T instance, Object message) throws IOException {
        HashMap<Player, WebSocketSession> map = instance.getPlayersWithSessions();
        for (Player p : map.keySet())
            sendMessageToPlayer(map.get(p), message);
    }

    public void sendMessageToPlayer(WebSocketSession playerSession, Object message) throws IOException {
        playerSession.sendMessage(new TextMessage(gson.toJson(message)));
    }

    /**
     * Handles the requests received by the handler and parsed by the request parser.
     *
     * @param session
     * @param request
     * @return true, if the request was handled successfully
     */
    protected boolean handleRequest(WebSocketSession session, BasicWebSocketRequest request) throws IOException {
        getLogger().log(this, String.format("RECEIVED: %s", gson.toJson(request)));

        if (request instanceof HelloGameRequest) {
            HelloGameRequest helloGameRequest = (HelloGameRequest) request;

            boolean contains = gameInstances.containsKey(helloGameRequest.lobbyName);
            // todo exception if !contains
            boolean identify = gameInstances.get(helloGameRequest.lobbyName).identifyPlayer(helloGameRequest.playerId, session);

            getLogger().log(this, String.format("received HelloGameRequest from Player(%s) for Lobby(%s)", helloGameRequest.playerId, helloGameRequest.lobbyName));

            if (contains) {
                sendMessageToPlayer(session, new HelloGameResponse(200, ""));
            } else
                sendMessageToPlayer(session, new HelloGameResponse(404, "Unkown Player"));

            return true;
        }
        return false;
    }

    /**
     * @return Parser to use for received messages.
     */
    protected abstract RequestParserBase getMessageParser();

    /**
     * @return Main logger instance
     */
    protected abstract ILogger getLogger();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        try {
            handleRequest(session, getMessageParser().parseMessage(message.getPayload()));
        } catch (IllegalArgumentException e) {
            getLogger().error(this, String.format("Failed to derive a type for message: %s", message.getPayload()));
        }
    }

    public void redirectToNextGame(T instance, LobbySocketHandler handler) {
        try {
            handler.redirectToNewGame(instance.lobbyId, instance.playerConnections);
        } catch (IOException e) {
            // todo add smart catch for exception
            getLogger().error(this, String.format("Forwarding to next game not possible in Lobby(%s) due to an IOException", instance.lobbyId));
        }
    }
}
