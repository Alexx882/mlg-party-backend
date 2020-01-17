package mlg.party.games.cocktail_shaker;

import com.google.gson.Gson;
import mlg.party.Callback;
import mlg.party.games.BasicGame;
import mlg.party.games.websocket.responses.GameFinishedResponse;
import mlg.party.lobby.games.GameFinishedArgs;
import mlg.party.lobby.websocket.responses.StartGameResponse;

public class CocktailShakerGame extends BasicGame<CocktailShakerSocketHandler> {

    private final String endpoint;
    private final Gson gson = new Gson();

    public CocktailShakerGame(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void startGame() {
        socketHandler.registerNewGameInstance(this);

        // inform players
        StartGameResponse response = new StartGameResponse(200, getGameEndpoint());
//        socketHandler.sendMessageToPlayers(this, gson.toJson(response));

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

    public void handleNewResult(String playerId, float max, float avg) {
        // assume there is just one player

        // finished:
        GameFinishedResponse response = new GameFinishedResponse(playerId);
//        socketHandler.sendMessageToPlayer(player);
    }
}
