package mlg.party.lobby.websocket.responses;

public class JoinLobbyResponse {
    private final int status;
    private final String playerid;
    private final String type = "CreateLobby";

    public JoinLobbyResponse(int status, String playerid) {
        this.status = status;
        this.playerid = playerid;
    }

    public int getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String getPlayerid() {
        return playerid;
    }
}
