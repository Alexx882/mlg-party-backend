package mlg.party.lobby.websocket.requests;

public class BasicWebSocketRequest {
    private final String type;

    public BasicWebSocketRequest(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
