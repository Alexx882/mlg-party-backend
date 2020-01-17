package mlg.party.games;

import mlg.party.lobby.lobby.Player;
import mlg.party.lobby.websocket.IRequestParser;
import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.http.WebSocket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class GameWebSocketHandler<T extends BasicGame> extends TextWebSocketHandler {
    protected Map<String, T> gameInstances = new ConcurrentHashMap<>();
    private Map<Player, WebSocketSession> sessions = new ConcurrentHashMap<>();

    /**
     * Registers a new game in the handler where actions can be handled
     *
     * @param instance  - instance of the game
     */
    public void registerNewGameInstance(T instance) {
        gameInstances.put(instance.lobbyId, instance);
    }

    public void closeGameInstance(T instance) {
        gameInstances.remove(instance.lobbyId);
    }

    public void sendMessageToPlayers(T instance, String message) {

    }

    public void sendMessageToPlayer(Player player, String message){

    }

    /**
     * Handles the requests received by the handler and parsed by the request parser.
     * @param session
     * @param request
     */
    protected abstract void handleRequest(WebSocketSession session, BasicWebSocketRequest request);

    // todo spring stuff for this
    /**
     * The parser to use for received messages.
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
