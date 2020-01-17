package mlg.party.games;

import com.google.gson.Gson;
import mlg.party.lobby.lobby.Player;
import mlg.party.lobby.websocket.IRequestParser;
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
public abstract class GameWebSocketHandler<T extends BasicGame> extends TextWebSocketHandler {
    protected Map<String, T> gameInstances = new ConcurrentHashMap<>();
    private Map<Player, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    /**
     * Registers a new game in the handler where actions can be handled
     *
     * @param instance - instance of the game
     */
    public void registerNewGameInstance(T instance) {
        // todo should be done all inside factory or all outside factory
        instance.setSocketHandler(this);
        gameInstances.put(instance.lobbyId, instance);
    }

    public void closeGameInstance(T instance) {
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
     */
    protected abstract void handleRequest(WebSocketSession session, BasicWebSocketRequest request);

    // todo spring stuff for this

    /**
     * The parser to use for received messages.
     *
     * @return
     */
    protected abstract IRequestParser getMessageParser();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            handleRequest(session, getMessageParser().parseMessage(message.getPayload()));
        } catch (IllegalArgumentException e) {
            // todo logging
//            logger.error(this, String.format("Failed to derive a type for message: %s", message.getPayload()));
            System.out.println(String.format("Failed to derive a type for message: %s", message.getPayload()));
        }
    }
}
