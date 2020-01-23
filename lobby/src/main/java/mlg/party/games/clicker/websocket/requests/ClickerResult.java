package mlg.party.games.clicker.websocket.requests;

import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;

public class ClickerResult extends BasicWebSocketRequest {

    public ClickerResult(String lobbyId, String playerId, float max) {
        super("ClickerResult");
        this.lobbyId = lobbyId;
        this.playerId = playerId;
        this.max = max;
    }

    public final String lobbyId;
    private String playerId;
    private float max;

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

}