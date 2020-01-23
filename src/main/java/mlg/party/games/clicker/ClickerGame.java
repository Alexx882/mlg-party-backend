package mlg.party.games.clicker;

import mlg.party.Callback;
import mlg.party.games.BasicGame;
import mlg.party.games.GameFinishedArgs;
import mlg.party.games.clicker.websocket.requests.ClickerResult;
import mlg.party.lobby.lobby.Player;

import java.util.ArrayList;
import java.util.List;

public class ClickerGame extends BasicGame<ClickerGame, ClickerSocketHandler> {

    private Callback<GameFinishedArgs> gameFinishedCallback = null;
    private final String endpoint;
    private List<ClickerResult> gameResults = new ArrayList<>(playerConnections.size());

    public ClickerGame(String lobbyId, List<Player> participants, String endpoint) {
        super(lobbyId, participants);
        this.endpoint = endpoint;
    }

    @Override
    public void startGame() {
        socketHandler.registerNewGameInstance(this);
    }

    @Override
    public String getGameName() {
        return "Clicker";
    }

    @Override
    public String getGameEndpoint() {
        return endpoint;
    }

    public void handleNewResult(ClickerResult result) {
        if (result == null)
            throw new IllegalArgumentException("result cannot be null");

        gameResults.add(result);

        if (gameResults.size() == players.size())
            manageGameFinished(gameResults);
    }

    private void manageGameFinished(List<ClickerResult> results) {
        ClickerResult best = results.get(0);
        for (ClickerResult cur : results)
            if (best.getMax() < cur.getMax())
                best = cur;

        super.notifyGameFinished(this, best.getPlayerId());

        socketHandler.removeGameInstance(this);
        socketHandler.redirectToNextGame(this);
    }
}
