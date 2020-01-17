package mlg.party.games.cocktail_shaker;

import com.google.gson.Gson;
import mlg.party.Callback;
import mlg.party.games.BasicGame;
import mlg.party.games.cocktail_shaker.websocket.requests.CocktailShakerResult;
import mlg.party.games.websocket.responses.GameFinishedResponse;
import mlg.party.lobby.games.GameFinishedArgs;
import mlg.party.lobby.websocket.responses.StartGameResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CocktailShakerGame extends BasicGame<CocktailShakerSocketHandler> {

    private final String endpoint;
    private List<CocktailShakerResult> gameResults = new ArrayList<>(players.size());

    public CocktailShakerGame(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void startGame() {
        socketHandler.registerNewGameInstance(this);

        // inform players
        StartGameResponse response = new StartGameResponse(200, getGameEndpoint());
        try {
            socketHandler.sendMessageToPlayers(this, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerResultCallback(Callback<GameFinishedArgs> callback) {

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

        if (gameResults.size() == players.size())
            manageGameFinished(gameResults);

//
    }

    private void manageGameFinished(List<CocktailShakerResult> results) {
// finished:
//        GameFinishedResponse response = new GameFinishedResponse(playerId);
//        try {
//            socketHandler.sendMessageToPlayers(this, response);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
