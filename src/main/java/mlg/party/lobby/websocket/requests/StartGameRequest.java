package mlg.party.lobby.websocket.requests;

public class StartGameRequest extends BasicWebSocketRequest {
    private String lobbyId;

    public StartGameRequest(String lobbyId) {
        super("StartGame");
        this.lobbyId = lobbyId;
    }

    public String getLobbyId() {
        return lobbyId;
    }
}
