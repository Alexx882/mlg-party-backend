package mlg.party.lobby.websocket.requests;

public class StartGameRequest extends BasicWebSocketRequest {
    private String lobbyName;

    StartGameRequest(String type, String lobbyName) {
        super(type);
        this.lobbyName = lobbyName;
    }

    public String getLobbyName() {
        return lobbyName;
    }
}
