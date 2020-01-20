package mlg.party.lobby.websocket.responses;

public class LobbyCreatedResponse extends BasicWebSocketResponse {
    public final String lobbyName;
    public final String playerId;

    public LobbyCreatedResponse(String name, String playerId) {
        super("CreateLobby");
        this.lobbyName = name;
        this.playerId = playerId;
    }
}
