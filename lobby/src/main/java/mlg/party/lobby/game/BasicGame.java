package mlg.party.lobby.game;

import mlg.party.Callback;
import mlg.party.lobby.lobby.Player;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;

public interface BasicGame {

    /**
     * Initializes the game with list of players.
     * @param lobbyId
     * @param players
     * @param socketHandler
     */
    void initialize(String lobbyId, List<Player> players, TextWebSocketHandler socketHandler);

    /**
     * Starts the game.
     */
    void startGame();

    /**
     * Registers a callback which gets called once the game is finished.
     */
    void registerResultCallback(Callback<GameFinishedArgs> callback);

}
