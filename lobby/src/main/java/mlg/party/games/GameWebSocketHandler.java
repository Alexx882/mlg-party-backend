package mlg.party.games;

import mlg.party.lobby.lobby.Player;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class GameWebSocketHandler<T extends BasicGame> extends TextWebSocketHandler {
    private CopyOnWriteArrayList<T> gameInstances = new CopyOnWriteArrayList<>();
    private Map<T, List<Player>> players = new ConcurrentHashMap<>();
    private Map<Player, WebSocketSession> sessions = new ConcurrentHashMap<>();

    /**
     * registers a new game in the handler where actions can be handled
     *
     * @param instance  - instance of the game
     * @param playerSet - participants
     */
    public void registerNewGameInstance(T instance, Set<Player> playerSet) {
        gameInstances.add(instance);
    }

    public void closeGameInstance(T instance) {
        gameInstances.remove(instance);
        players.remove(instance);
    }

    public void sendMessageToPlayers(T instance, String message) {
    }
}
