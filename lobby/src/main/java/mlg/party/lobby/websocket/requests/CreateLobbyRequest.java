package mlg.party.lobby.websocket.requests;

/**
 * {"type":"CreateLobby","playerName":"Lerchner"}
 */
public class CreateLobbyRequest extends AbstractWebsocketRequest {
    private final String playerName;

    public CreateLobbyRequest(String type, String playerName) {
        super(type);
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }
}
