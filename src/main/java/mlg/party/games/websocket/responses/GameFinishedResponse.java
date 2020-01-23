package mlg.party.games.websocket.responses;

import mlg.party.lobby.lobby.Player;

import java.util.List;
import java.util.Objects;

/**
 * Who won the game.
 */
public class GameFinishedResponse {
    public final String type = "GameFinished";
    public final String winnerId;
    public final List<Player> ranking;

    public GameFinishedResponse(String winnerId, List<Player> ranking) {
        this.winnerId = winnerId;
        this.ranking = ranking;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GameFinishedResponse
                && this.type == ((GameFinishedResponse) obj).type
                && this.winnerId == ((GameFinishedResponse) obj).winnerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, winnerId);
    }
}
