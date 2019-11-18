package mlg.party.lobby.websocket.requests;

/**
 * {"type":"JoinLobby","lobbyName":"0298","playerName":"Henri"}
 */
public class JoinLobbyRequest extends AbstractWebsocketRequest {
    private final String lobbyName;
    private final String playerName;

    public JoinLobbyRequest(String type, String lobbyName, String playerName) {
        super(type);
        this.lobbyName = lobbyName;
        this.playerName = playerName;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public String getPlayerName() {
        return playerName;
    }
}
