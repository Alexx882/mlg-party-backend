package mlg.party.games.spacepirates;

import mlg.party.games.BasicGame;
import mlg.party.games.spacepirates.websocket.SpacePiratesSocketHandler;
import mlg.party.games.spacepirates.websocket.requests.SpacePiratesResult;
import mlg.party.lobby.lobby.Player;

import java.util.ArrayList;
import java.util.List;

public class SpacePiratesGame extends BasicGame<SpacePiratesGame, SpacePiratesSocketHandler> {

    private final String endpoint;
    private List<SpacePiratesResult> gameResults = new ArrayList<>(playerConnections.size());

    public SpacePiratesGame(String lobbyId, List<Player> participants, String endpoint) {
        super(lobbyId, participants);
        this.endpoint = endpoint;
    }

    @Override
    public void startGame() {
        socketHandler.registerNewGameInstance(this);
    }

    @Override
    public String getGameName() {
        return "Spacepirate";
    }

    @Override
    public String getGameEndpoint() {
        return endpoint;
    }

    public void handleNewResult(SpacePiratesResult result) {
        if (result == null)
            throw new IllegalArgumentException("result cannot be null");

        gameResults.add(result);

        if (gameResults.size() == players.size())
            manageGameFinished(gameResults);
    }

    private void manageGameFinished(List<SpacePiratesResult> results) {
        SpacePiratesResult best = results.get(0);
        for (SpacePiratesResult cur : results)
            if (best.getMax() < cur.getMax())
                best = cur;

        super.notifyGameFinished(this, best.getPlayerId());

        socketHandler.removeGameInstance(this);
        socketHandler.redirectToNextGame(this);
    }
}
