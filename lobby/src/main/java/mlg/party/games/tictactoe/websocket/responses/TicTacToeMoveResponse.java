package mlg.party.games.tictactoe.websocket.responses;

import mlg.party.lobby.websocket.responses.BasicWebSocketResponse;

public class TicTacToeMoveResponse extends BasicWebSocketResponse {
    public String playerId;
    public int x;
    public int y;

    public TicTacToeMoveResponse(String playerId, int x, int y) {
        super("TicTacToeMove");
        this.playerId = playerId;
        this.x = x;
        this.y = y;
    }
}
