package mlg.party.games;

import mlg.party.Callback;
import mlg.party.games.websocket.responses.GameFinishedResponse;
import mlg.party.lobby.lobby.Player;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Contains information about the game like lobby and players and the game logic.
 *
 * @param <T> - generic Sockethandler which can communicate with the other players
 */
public abstract class BasicGame<G extends BasicGame, T extends GameWebSocketHandler<G>> {
    protected final String lobbyId;
    protected final List<Player> players;
    protected HashMap<Player, WebSocketSession> playerConnections;
    protected T socketHandler;
    private Callback<GameFinishedArgs> gameFinishedCallback = null;

    public BasicGame(String lobbyId, List<Player> players) {
        this.lobbyId = lobbyId;
        this.players = players;

        playerConnections = new HashMap<>();
    }

    public boolean identifyPlayer(String playerId, WebSocketSession session) {
        if (playerId == null)
            throw new IllegalArgumentException();

        for (Player p : players) {
            if (p.getId().equals(playerId) && !playerConnections.containsKey(p)) {
                playerConnections.put(p, session);
                return true;
            }
        }

        return false;
    }

    /**
     * @return true, iff every player identified himself to the server
     */
    public boolean isReadyToPlay() {
        for (Player player : players)
            if (!playerConnections.containsKey(player))
                return false;

        return true;
    }

    /**
     * Sets the socket handler to use for messages received from endpoint getGameEndpoint().
     *
     * @param socketHandler
     */
    public void setSocketHandler(T socketHandler) {
        this.socketHandler = socketHandler;
    }

    public HashMap<Player, WebSocketSession> getPlayersWithSessions() {
        return playerConnections;
    }

    /**
     * Starts the game.
     */
    public abstract void startGame();

    /**
     * Registers a callback which gets called once the game is finished.
     */
    public void registerGameFinishedCallback(Callback<GameFinishedArgs> callback){
        gameFinishedCallback = callback;
    }

    /**
     * Notify connected players and raise the game finished callback.
     * @param winnerId The playerId of the winner.
     * @param game
     */
    public void notifyGameFinished(G game, String winnerId){
        try {
            GameFinishedResponse response = new GameFinishedResponse(winnerId);
            socketHandler.sendMessageToPlayers(game, response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (gameFinishedCallback != null) {
            GameFinishedArgs args = new GameFinishedArgs(lobbyId, winnerId);
            gameFinishedCallback.callback(args);
        }
    }

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
