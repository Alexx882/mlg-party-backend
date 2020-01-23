package mlg.party.lobby.websocket.requests;

/**
 * {"type":"CreateLobby","playerName":"Lercher"}
 */
public class CreateLobbyRequest extends BasicWebSocketRequest {
    private final String playerName;

    public CreateLobbyRequest(String playerName) {
        super("CreateLobby");
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }
}