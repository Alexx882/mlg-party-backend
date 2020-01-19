package mlg.party.games;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

@ClientEndpoint
public class TestWebSocketClient {
    Session session;

    public final String name;
    private Queue<String> replies = new LinkedList<>();

    public TestWebSocketClient(String name) {
        this.name = name;
    }

    @OnOpen
    public void onOpen(final Session session) {
        this.session = session;
    }

    private void print(String message) {
        System.out.println(String.format("[%s]: %s", name, message));
    }

    @OnMessage
    public void onMessage(String message) {
        print("RECEIVED " + message);
        replies.add(message);
    }

    public void send(String message) throws IOException {
        print("SEND " + message);
        session.getBasicRemote().sendText(message);
    }

    public void waitForResponse() {
        while(replies.isEmpty());
    }

    public Queue<String> getReplies() {
        return replies;
    }
}
