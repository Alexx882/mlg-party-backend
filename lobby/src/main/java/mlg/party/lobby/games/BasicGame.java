package mlg.party.lobby.games;

import mlg.party.Callback;


public interface BasicGame {

    /**
     * Initializes the game with parameters.
     * @param params
     */
    void initialize(GameParameters params);

    /**
     * Starts the game.
     */
    void startGame();

    /**
     * Registers a callback which gets called once the game is finished.
     */
    void registerResultCallback(Callback<GameFinishedArgs> callback);

}
