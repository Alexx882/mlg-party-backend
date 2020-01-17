package mlg.party.games.websocket.responses;

/**
 * Who won the game.
 */
public class GameFinishedResponse {
    public String type = "GameFinishedResponse";
    public String winnerId;

    public GameFinishedResponse(String winnerId){
        this.winnerId = winnerId;
    }
}
