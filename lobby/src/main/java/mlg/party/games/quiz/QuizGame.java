package mlg.party.games.quiz;

import mlg.party.Callback;
import mlg.party.games.BasicGame;
import mlg.party.games.GameFinishedArgs;
import mlg.party.games.cocktail_shaker.CocktailShakerSocketHandler;
import mlg.party.games.cocktail_shaker.websocket.requests.CocktailShakerResult;
import mlg.party.games.quiz.websocket.requests.QuizResult;
import mlg.party.games.websocket.responses.GameFinishedResponse;
import mlg.party.lobby.lobby.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuizGame extends BasicGame<QuizSocketHandler> {

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

    @Override
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
        if(result == null)
            throw new IllegalArgumentException("result cannot be null");

        gameResults.add(result);

        if (gameResults.size() == players.size())
            manageGameFinished(gameResults);
    }

    private void manageGameFinished(List<QuizResult> results) {
        QuizResult player1 = results.get(0);
        QuizResult player2 = results.get(1);
        QuizResult best = new QuizResult(lobbyId, "Draw", false);


         if (player1.won && !player2.won) {
            best = player1;
        } else if (player2.won && !player1.won) {
            best = player2;
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
