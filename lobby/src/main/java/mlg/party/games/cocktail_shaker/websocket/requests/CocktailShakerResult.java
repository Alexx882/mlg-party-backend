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

    public String lobbyId;
    public String playerId;
    public float max;
    public float avg;

}