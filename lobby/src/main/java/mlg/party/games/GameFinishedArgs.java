package mlg.party.games;

public class GameFinishedArgs {
    public String lobbyId;
    public String winnerId;

    public GameFinishedArgs(String lobbyId, String winnerId) {
        this.lobbyId = lobbyId;
        this.winnerId = winnerId;
    }
}
