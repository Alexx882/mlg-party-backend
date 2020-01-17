package mlg.party.games;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Factory for different games
 * @param <T> A game class extending BasicGame
 */
public abstract class GameFactory<T extends BasicGame> {
    private static List<GameFactory<?>> factories = new LinkedList<>();

    public void registerFactory(GameFactory<?> factory) {
        factories.add(factory);
    }

    public static GameFactory<?> getRandomGameFactory() {
        Collections.shuffle(factories);
        return factories.get(0);
    }

    public abstract T createGame();
}
