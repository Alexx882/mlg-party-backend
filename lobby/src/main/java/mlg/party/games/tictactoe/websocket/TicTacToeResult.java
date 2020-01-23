package mlg.party.games.tictactoe.websocket;

import mlg.party.games.websocket.requests.GameResultRequest;

public class TicTacToeResult extends GameResultRequest {

    public TicTacToeResult(String lobbyId, String playerId, boolean won) {
        super("TicTacToeResult", lobbyId);
        this.playerId = playerId;
        this.won = won;
    }

    public String playerId;
    public boolean won;
}