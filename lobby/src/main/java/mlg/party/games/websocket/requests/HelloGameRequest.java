package mlg.party.games.websocket.requests;

import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;

public class HelloGameRequest extends BasicWebSocketRequest {

    public final String playerId;
    public final String lobbyName;

    public HelloGameRequest(String type, String playerId, String lobbyName) {
        super("HelloGame");
        this.playerId = playerId;
        this.lobbyName = lobbyName;
    }
}
