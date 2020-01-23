package mlg.party.lobby.websocket.responses;

public class StartGameResponse extends BasicWebSocketResponse {
    public final int status;
    public final String gameEndpoint;

    public StartGameResponse(int status, String gameEndpoint) {
        super("StartGame");
        this.status = status;
        this.gameEndpoint = gameEndpoint;
    }
}
