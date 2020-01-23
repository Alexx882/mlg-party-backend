package mlg.party.games.tictactoe.websocket.requests;

import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;

public class TicTacToeMoveRequest extends BasicWebSocketRequest {
    public String lobbyId;
    public String playerId;
    public int x;
    public int y;

    public TicTacToeMoveRequest(String type, String playerId, int x, int y) {
        super("TioTacToeMove");
        this.playerId = playerId;
        this.x = x;
        this.y = y;
    }
}
