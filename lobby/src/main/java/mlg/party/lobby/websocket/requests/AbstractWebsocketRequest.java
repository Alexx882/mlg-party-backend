package mlg.party.lobby.websocket.requests;

public abstract class AbstractWebsocketRequest {
    private final String type;

    AbstractWebsocketRequest(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
