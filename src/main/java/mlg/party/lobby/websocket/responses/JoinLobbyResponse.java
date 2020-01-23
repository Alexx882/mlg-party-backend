package mlg.party.lobby.websocket.responses;

public class JoinLobbyResponse extends BasicWebSocketResponse {
    private final int status;
    private final String playerId;

    public JoinLobbyResponse(int status, String playerId) {
        super("JoinLobby");
        this.status = status;
        this.playerId = playerId;
    }

    public int getStatus() {
        return status;
    }

    public String getPlayerId() {
        return playerId;
    }
}
