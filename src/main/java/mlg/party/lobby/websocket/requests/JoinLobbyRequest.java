package mlg.party.lobby.websocket.requests;

/**
 * {"type":"JoinLobby","lobbyId":"0298","playerName":"Herry"}
 */
public class JoinLobbyRequest extends BasicWebSocketRequest {
    private final String lobbyId;
    private String playerName;

    public JoinLobbyRequest(String lobbyId, String playerName) {
        super("JoinLobby");
        this.lobbyId = lobbyId;
        this.playerName = playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public String getPlayerName() {
        return playerName;
    }
}
