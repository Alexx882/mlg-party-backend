package mlg.party.lobby.websocket.responses;

public class StartGameResponse  {
    public final String type = "StartGame";
    public final int status;
    public final String gameEndpoint;

    public StartGameResponse(int status, String gameEndpoint) {
        this.status = status;
        this.gameEndpoint = gameEndpoint;
    }
}
