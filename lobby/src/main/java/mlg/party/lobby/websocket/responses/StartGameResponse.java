package mlg.party.lobby.websocket.responses;

public class StartGameResponse  {
    private String type = "StartGame";
    private int status;
    private String gameEndpoint;

    public StartGameResponse(int status, String gameEndpoint) {
        this.status = status;
        this.gameEndpoint = gameEndpoint;
    }
}
