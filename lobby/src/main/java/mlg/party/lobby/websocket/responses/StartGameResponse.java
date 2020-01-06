package mlg.party.lobby.websocket.responses;

public class StartGameResponse  {
    private String type = "StartGame";
    private int status;
    private String gameName;

    public StartGameResponse(int status, String gameName) {
        this.status = status;
        this.gameName = gameName;
    }
}
