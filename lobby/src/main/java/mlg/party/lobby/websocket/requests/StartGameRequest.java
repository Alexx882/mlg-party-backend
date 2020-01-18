package mlg.party.lobby.websocket.requests;

public class StartGameRequest extends BasicWebSocketRequest {
    private String lobbyName;

    public StartGameRequest(String lobbyName) {
        super("StartGame");
        this.lobbyName = lobbyName;
    }

    public String getLobbyName() {
        return lobbyName;
    }
}
