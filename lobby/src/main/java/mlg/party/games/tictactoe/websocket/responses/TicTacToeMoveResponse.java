package mlg.party.games.tictactoe.websocket.responses;

import mlg.party.lobby.websocket.responses.BasicWebSocketResponse;

public class TicTacToeMoveResponse extends BasicWebSocketResponse {
    public String playerId;
    public String lobbyId;
    public int x;
    public int y;

    public TicTacToeMoveResponse(String playerId,String lobbyId, int x, int y) {
        super("TicTacToeMove");
        this.playerId = playerId;
        this.lobbyId=lobbyId;
        this.x = x;
        this.y = y;
    }
}
