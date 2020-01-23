package mlg.party.games.quiz.websocket.requests;

import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;

public class QuizResult extends BasicWebSocketRequest {

    public QuizResult(String lobbyId, String playerId, boolean won) {
        super("QuizResult");
        this.lobbyId = lobbyId;
        this.playerId = playerId;
        this.won = won;
    }

    public String lobbyId;
    public String playerId;
    public boolean won;

}