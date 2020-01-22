package mlg.party.games.websocket.requests;

import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;

public class HelloGameRequest extends BasicWebSocketRequest {

    public String playerId;
    public final String lobbyName;

    public HelloGameRequest(String playerId, String lobbyName) {
        super("HelloGame");
        this.playerId = playerId;
        this.lobbyName = lobbyName;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}
