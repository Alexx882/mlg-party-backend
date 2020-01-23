package mlg.party.games;

import mlg.party.games.tictactoe.TicTacToeFactory;
import mlg.party.lobby.lobby.Player;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Factory for different games
 *
 * @param <T> A game class extending BasicGame
 */
public abstract class GameFactory<T extends BasicGame<?, ?>> {
    private static List<GameFactory<?>> factories = new LinkedList<>();
    private static int lastPlayedGame = 0;

    public void registerFactory(GameFactory<?> factory) {
        factories.add(factory);
    }

    private static boolean tmPTest = false;

    public static GameFactory<?> getRandomGameFactory() {
        GameFactory<?> result = factories.get(lastPlayedGame++);
        lastPlayedGame = lastPlayedGame % factories.size();
        return result;
    }

    /**
     * creates a new game instance
     *
     * @param lobbyId - identifies a lobby uniquely
     * @param players - participants of the game
     * @return new game instance
     */
    public abstract T createGame(String lobbyId, List<Player> players);
}
