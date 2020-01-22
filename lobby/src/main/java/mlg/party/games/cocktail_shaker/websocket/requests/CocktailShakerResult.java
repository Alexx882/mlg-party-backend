package mlg.party.games.cocktail_shaker.websocket.requests;

import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;

public class CocktailShakerResult extends BasicWebSocketRequest {

    public CocktailShakerResult(String lobbyId, String playerId, float max, float avg) {
        super("CocktailShakerResult");
        this.lobbyId = lobbyId;
        this.playerId = playerId;
        this.max = max;
        this.avg = avg;
    }

    public final String lobbyId;
    private String playerId;
    private float max;
    private float avg;

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

    public float getAvg() {
        return avg;
    }

    public void setAvg(float avg) {
        this.avg = avg;
    }
}