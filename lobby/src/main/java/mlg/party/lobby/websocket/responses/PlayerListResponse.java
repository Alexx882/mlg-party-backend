package mlg.party.lobby.websocket.responses;

import java.util.List;

public class PlayerListResponse extends BasicWebSocketResponse {
    public final List<String> playerNames;

    public PlayerListResponse(List<String> playerNames) {
        super("PlayerJoined");
        this.playerNames = playerNames;
    }
}
