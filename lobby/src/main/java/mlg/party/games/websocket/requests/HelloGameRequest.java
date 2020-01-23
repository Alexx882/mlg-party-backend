package mlg.party.games.websocket.requests;

import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;

public class HelloGameRequest extends BasicWebSocketRequest {

    public String playerId;
    public final String lobbyId;

    public HelloGameRequest(String playerId, String lobbyId) {
        super("HelloGame");
        this.playerId = playerId;
        this.lobbyId = lobbyId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}
