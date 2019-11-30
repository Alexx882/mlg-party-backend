package mlg.party.lobby.websocket;

import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;

public interface IRequestParser {
    BasicWebSocketRequest parseMessage(String json);
}
