package mlg.party.lobby.games;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.io.IOException;

@ClientEndpoint
public class TestWebSocketClient {
    Session session;

    @OnOpen
    public void onOpen(final Session session) {
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println(String.format("RECEIVED MESSAGE: %s", message));
    }

    public void send(String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }
}
