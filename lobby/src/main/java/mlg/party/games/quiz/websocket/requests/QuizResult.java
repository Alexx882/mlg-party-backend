package mlg.party.games.quiz.websocket.requests;

import mlg.party.games.websocket.requests.GameResultRequest;

public class QuizResult extends GameResultRequest {

    public QuizResult(String lobbyId, String playerId, boolean won) {
        super("QuizResult", lobbyId);
        this.playerId = playerId;
        this.won = won;
    }

    public String playerId;
    public boolean won;

}