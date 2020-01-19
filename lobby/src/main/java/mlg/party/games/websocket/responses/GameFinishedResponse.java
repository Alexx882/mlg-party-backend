package mlg.party.games.websocket.responses;

/**
 * Who won the game.
 */
public class GameFinishedResponse {
    public String type = "GameFinished";
    public String winnerId;

    public GameFinishedResponse(String winnerId) {
        this.winnerId = winnerId;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GameFinishedResponse
                && this.type == ((GameFinishedResponse) obj).type
                && this.winnerId == ((GameFinishedResponse) obj).winnerId;
    }
}
