package mlg.party.lobby.websocket;

import mlg.party.lobby.websocket.requests.AbstractWebsocketRequest;

public interface IRequestParser {
    AbstractWebsocketRequest parseMessage(String json);
}
