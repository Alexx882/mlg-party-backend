package mlg.party.lobby.websocket.responses;

public class LobbyCreatedResponse extends BasicWebSocketResponse {
    public final String lobbyId;
    public final String playerId;

    public LobbyCreatedResponse(String name, String playerId) {
        super("CreateLobby");
        this.lobbyId = name;
        this.playerId = playerId;
    }
}
