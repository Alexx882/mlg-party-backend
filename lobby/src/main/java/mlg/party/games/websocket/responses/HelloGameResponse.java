package mlg.party.games.websocket.responses;

public class HelloGameResponse {
    public final int status;
    public final String type;

    public HelloGameResponse(int status) {
        this.status = status;
        this.type = "HelloGame";
    }
}
