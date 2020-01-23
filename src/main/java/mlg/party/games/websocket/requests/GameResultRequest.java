package mlg.party.games.websocket.requests;

import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;

public abstract class GameResultRequest extends BasicWebSocketRequest {
    public String lobbyId;

    public GameResultRequest(String type, String lobbyId) {
        super(type);
        this.lobbyId = lobbyId;
    }
}
