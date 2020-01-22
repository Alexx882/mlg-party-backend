package mlg.party.games.rps;

import mlg.party.Callback;
import mlg.party.games.BasicGame;
import mlg.party.games.GameFinishedArgs;
import mlg.party.games.rps.websocket.requests.RpsResult;
import mlg.party.games.websocket.responses.GameFinishedResponse;
import mlg.party.lobby.lobby.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RpsGame extends BasicGame<RpsGame, RpsSocketHandler> {

    private Callback<GameFinishedArgs> gameFinishedCallback = null;
    private final String endpoint;
    private List<RpsResult> gameResults = new ArrayList<>(playerConnections.size());

    public RpsGame(String lobbyId, List<Player> participants, String endpoint) {
        super(lobbyId, participants);
        this.endpoint = endpoint;
    }

    @Override
    public void startGame() {
        socketHandler.registerNewGameInstance(this);
    }
    
    public void registerResultCallback(Callback<GameFinishedArgs> callback) {
        gameFinishedCallback = callback;
    }

    @Override
    public String getGameName() {
        return "Rps";
    }

    @Override
    public String getGameEndpoint() {
        return endpoint;
    }

    public void handleNewResult(RpsResult result) {
        if(result == null)
            throw new IllegalArgumentException("result cannot be null");

        gameResults.add(result);

        if (gameResults.size() == players.size())
            manageGameFinished(gameResults);
    }

    private void manageGameFinished(List<RpsResult> results) {
        RpsResult player1 = results.get(0);
        RpsResult player2 = results.get(1);
        RpsResult best = null;
        RpsLogic logic = new RpsLogic();
        RpsLogic.Result result = logic.checkResult(player1.option, player2.option);

        switch (result) {
            case WON:
                best = player1;
                break;
            case LOST:
                best = player2;
                break;
            case DRAW:
                best = new RpsResult(lobbyId,"Draw",null);
                break;
            default:
                best = new RpsResult(lobbyId,"Error",null);
        }

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
