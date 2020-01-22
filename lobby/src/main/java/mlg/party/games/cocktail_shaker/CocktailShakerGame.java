package mlg.party.games.cocktail_shaker;

import mlg.party.games.BasicGame;
import mlg.party.games.cocktail_shaker.websocket.CocktailShakerSocketHandler;
import mlg.party.games.cocktail_shaker.websocket.requests.CocktailShakerResult;
import mlg.party.lobby.lobby.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CocktailShakerGame extends BasicGame<CocktailShakerGame, CocktailShakerSocketHandler> {

    private final String endpoint;
    private List<CocktailShakerResult> gameResults = new ArrayList<>(playerConnections.size());

    public CocktailShakerGame(String lobbyId, List<Player> participants, String endpoint) {
        super(lobbyId, participants);
        this.endpoint = endpoint;
    }

    @Override
    public void startGame() {
        socketHandler.registerNewGameInstance(this);
    }

    @Override
    public String getGameName() {
        return "CocktailShaker";
    }

    @Override
    public String getGameEndpoint() {
        return endpoint;
    }

    public void handleNewResult(CocktailShakerResult result) {
        if (result == null)
            throw new IllegalArgumentException("result cannot be null");

        gameResults.add(result);

        if (gameResults.size() == players.size())
            manageGameFinished(gameResults);
    }

    private void manageGameFinished(List<CocktailShakerResult> results) {
        CocktailShakerResult best = results.get(0);
        for (CocktailShakerResult cur : results)
            if (best.getAvg() < cur.getAvg())
                best = cur;

        for (Player player : players)
            if (player.getId().equals(best.getPlayerId()))
                player.increasePoints();

        players.sort((p1, p2) -> p2.getPoints() - p1.getPoints());

        super.notifyGameFinished(this, best.getPlayerId());

        socketHandler.removeGameInstance(this);
        socketHandler.redirectToNextGame(this);
    }
}
