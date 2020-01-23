package mlg.party.games.quiz;

import mlg.party.Callback;
import mlg.party.games.BasicGame;
import mlg.party.games.GameFinishedArgs;
import mlg.party.games.quiz.websocket.requests.QuizResult;
import mlg.party.games.websocket.responses.GameFinishedResponse;
import mlg.party.lobby.lobby.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class QuizGame extends BasicGame<QuizGame, QuizSocketHandler> {

    private Callback<GameFinishedArgs> gameFinishedCallback = null;
    private final String endpoint;
    private List<QuizResult> gameResults = new ArrayList<>(playerConnections.size());

    public QuizGame(String lobbyId, List<Player> participants, String endpoint) {
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
        return "Quiz";
    }

    @Override
    public String getGameEndpoint() {
        return endpoint;
    }

    public void handleNewResult(QuizResult result) {
        if (result == null)
            throw new IllegalArgumentException("result cannot be null");

        gameResults.add(result);

        if (gameResults.size() == players.size())
            manageGameFinished(gameResults);
    }

    private void manageGameFinished(List<QuizResult> results) {
        List<QuizResult> winners = results.stream().filter((r) -> r.won).collect(Collectors.toList());
        QuizResult best = winners.size() > 0 ? winners.get(0) : new QuizResult(lobbyId, "Draw", false);

        for (QuizResult winner : winners)
            for (Player player : players)
                if (player.getId().equals(winner.playerId))
                    player.increasePoints();

        players.sort((p1, p2) -> p2.getPoints() - p1.getPoints());

        try {
            GameFinishedResponse response = new GameFinishedResponse(best.playerId, players);
            socketHandler.sendMessageToPlayers(this, response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (gameFinishedCallback != null) {
            GameFinishedArgs args = new GameFinishedArgs(lobbyId, best.playerId);
            gameFinishedCallback.callback(args);
        }

        socketHandler.removeGameInstance(this);
        socketHandler.redirectToNextGame(this);
    }
}
