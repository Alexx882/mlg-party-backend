package mlg.party.lobby.websocket.requests;

/**
 * {"type":"JoinLobby","lobbyName":"0298","playerName":"Herry"}
 */
public class JoinLobbyRequest extends BasicWebSocketRequest {
    private final String lobbyName;
    private String playerName;

    public JoinLobbyRequest(String lobbyName, String playerName) {
        super("JoinLobby");
        this.lobbyName = lobbyName;
        this.playerName = playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public String getPlayerName() {
        return playerName;
    }
}
