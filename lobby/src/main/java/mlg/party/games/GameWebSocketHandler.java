package mlg.party.games;

import com.google.gson.Gson;
import mlg.party.games.websocket.requests.HelloGameRequest;
import mlg.party.games.websocket.responses.HelloGameResponse;
import mlg.party.lobby.lobby.Player;
import mlg.party.lobby.logging.ILogger;
import mlg.party.RequestParserBase;
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
        if (request instanceof HelloGameRequest) {
            HelloGameRequest helloGameRequest = (HelloGameRequest) request;
            getLogger().log(this, String.format("received HelloGameRequest from Player(%s) for Lobby(%s)", helloGameRequest.playerId, helloGameRequest.lobbyName));

            if (gameInstances.containsKey(helloGameRequest.lobbyName) && gameInstances.get(helloGameRequest.lobbyName).identifyPlayer(helloGameRequest.playerId, session)) {
                sendMessageToPlayer(session, new HelloGameResponse(200));
            } else
                sendMessageToPlayer(session, new HelloGameResponse(404));

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
}
