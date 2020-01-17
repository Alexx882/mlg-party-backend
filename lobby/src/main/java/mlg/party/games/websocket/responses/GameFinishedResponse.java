package mlg.party.games.websocket.responses;

public class GameFinishedResponse {
    public String type = "GameFinishedResponse";
    public String winnerId;

    public GameFinishedResponse(String winnerId){
        this.winnerId = winnerId;
    }
}
