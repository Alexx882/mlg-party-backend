package mlg.party.lobby.websocket.responses;

import java.util.List;

public class PlayerListResponse {
    private final String type = "PlayerJoined";
    private final List<String> playerNames;

    public PlayerListResponse(List<String> playerNames) {
        this.playerNames = playerNames;
    }
}
