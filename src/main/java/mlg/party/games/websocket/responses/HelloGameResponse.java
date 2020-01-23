package mlg.party.games.websocket.responses;

public class HelloGameResponse {
    public final int status;
    public final String type;
    public final String message;

    public HelloGameResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.type = "HelloGame";
    }
}
