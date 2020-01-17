package mlg.party.games;

import mlg.party.Callback;
import mlg.party.lobby.games.GameFinishedArgs;
import mlg.party.lobby.lobby.Player;
import mlg.party.lobby.websocket.SocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;


public abstract class BasicGame {
    protected String lobbyId;
    protected HashMap<Player, WebSocketSession> players;
    protected GameWebSocketHandler<BasicGame> socketHandler;

    /**
     * Initializes the game with parameters.
     * @param lobbyId
     * @param players
     */
    public void initialize(String lobbyId, HashMap<Player, WebSocketSession> players) {
        this.lobbyId = lobbyId;
        this.players = players;
    }

    /**
     * Sets the socket handler to use for messages received from endpoint getGameEndpoint().
     * @param socketHandler
     */
    public void setSocketHandler(GameWebSocketHandler<BasicGame> socketHandler) {
        this.socketHandler = socketHandler;
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
     * @return
     */
    public abstract String getGameName();

    /**
     * Returns the websocket endpoint of the game.
     * @return
     */
    public abstract String getGameEndpoint();

}
