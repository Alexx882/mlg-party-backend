package mlg.party.games.cocktail_shaker;

import mlg.party.Callback;
import mlg.party.games.BasicGame;
import mlg.party.games.cocktail_shaker.websocket.requests.CocktailShakerResult;
import mlg.party.games.websocket.responses.GameFinishedResponse;
import mlg.party.games.GameFinishedArgs;
import mlg.party.lobby.lobby.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CocktailShakerGame extends BasicGame<CocktailShakerSocketHandler> {

    private Callback<GameFinishedArgs> gameFinishedCallback = null;
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
    public void registerResultCallback(Callback<GameFinishedArgs> callback) {
        gameFinishedCallback = callback;
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
        gameResults.add(result);

        if (gameResults.size() == playerConnections.size())
            manageGameFinished(gameResults);
    }

    private void manageGameFinished(List<CocktailShakerResult> results) {
        CocktailShakerResult best = results.get(0);
        for (CocktailShakerResult cur : results)
            if (best.avg < cur.avg)
                best = cur;

        try {
            GameFinishedResponse response = new GameFinishedResponse(best.playerId);
            socketHandler.sendMessageToPlayers(this, response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (gameFinishedCallback != null) {
            GameFinishedArgs args = new GameFinishedArgs(lobbyId, best.playerId);
            gameFinishedCallback.callback(args);
        }

        socketHandler.removeGameInstance(this);
        // todo herold pass back to lobby
    }
}
