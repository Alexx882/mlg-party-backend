package mlg.party.games;

import mlg.party.Callback;
import mlg.party.lobby.games.GameFinishedArgs;
import mlg.party.lobby.lobby.Player;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;

/**
 * Contains information about the game like lobby and players and the game logic.
 *
 * @param <T> - generic Sockethandler which can communicate with the other players
 */
public abstract class BasicGame<T extends GameWebSocketHandler<?>> {
    protected String lobbyId;
    protected HashMap<Player, WebSocketSession> players;
    protected T socketHandler;

    /**
     * Initializes the game with parameters.
     *
     * @param lobbyId
     * @param players
     */
    public void initialize(String lobbyId, HashMap<Player, WebSocketSession> players) {
        this.lobbyId = lobbyId;
        this.players = players;
    }

    /**
     * Sets the socket handler to use for messages received from endpoint getGameEndpoint().
     *
     * @param socketHandler
     */
    public void setSocketHandler(T socketHandler) {
        this.socketHandler = socketHandler;
    }

    public HashMap<Player, WebSocketSession> getPlayersWithSessions(){
        return players;
    }

    /**
     * Starts the game.
     */
    public abstract void startGame();

    /**
     * Registers a callback which gets called once the game is finished.
     */
    public abstract void registerResultCallback(Callback<GameFinishedArgs> callback);

    /**
     * Returns the name of the game.
     *
     * @return
     */
    public abstract String getGameName();

    /**
     * Returns the websocket endpoint of the game.
     *
     * @return
     */
    public abstract String getGameEndpoint();

}
