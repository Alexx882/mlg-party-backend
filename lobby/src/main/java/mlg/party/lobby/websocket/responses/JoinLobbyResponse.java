package mlg.party.lobby.websocket.responses;

public class JoinLobbyResponse extends BasicWebSocketResponse {
    private final int status;
    private final String playerid;

    public JoinLobbyResponse(int status, String playerid) {
        super("JoinLobby");
        this.status = status;
        this.playerid = playerid;
    }

    public int getStatus() {
        return status;
    }

    public String getPlayerid() {
        return playerid;
    }
}
