package mlg.party.games.tictactoe.websocket.responses;

import mlg.party.lobby.websocket.responses.BasicWebSocketResponse;

public class TicTacToeErrorResponse extends BasicWebSocketResponse {
    public String errorMessage;

    public TicTacToeErrorResponse(String errorMessage) {
        super("TicTacToeError");
        this.errorMessage = errorMessage;
    }
}
