package mlg.party.lobby.websocket.responses;

public class LobbyCreatedResponse {
    private final String type = "CreateLobby";

    private final String lobbyName;
    private final String playerId;

    public LobbyCreatedResponse(String name, String playerId) {
        this.lobbyName = name;
        this.playerId = playerId;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getType() {
        return type;
    }
}
