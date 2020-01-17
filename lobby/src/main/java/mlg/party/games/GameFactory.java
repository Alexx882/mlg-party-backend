package mlg.party.games;

public interface GameFactory<T extends BasicGame> {
    T createGame();
}
