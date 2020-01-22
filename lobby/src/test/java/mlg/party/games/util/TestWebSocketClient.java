package mlg.party.games.util;

import javax.websocket.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;

@ClientEndpoint
public class TestWebSocketClient {
    Session session;

    public final String name;
    private String id;

    private Queue<String> replies = new LinkedList<>();

    public TestWebSocketClient(String name) {
        this.name = name;
    }

    public Callable<Boolean> isOpen() {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return session != null && session.isOpen();
            }
        };
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

    @OnError
    public void onError(Session session, Throwable exception) {
        System.out.println(String.format("ERROR in websocket: %s", exception));
    }

    public void send(String message) throws IOException {
        print("SEND " + message);
        session.getBasicRemote().sendText(message);
    }

    public boolean hasReply() {
        return !replies.isEmpty();
    }

    public Callable<Boolean> hasReplyCallable() {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return hasReply();
            }
        };
    }

    public String popReply() {
        if (replies.isEmpty())
            throw new IllegalStateException(String.format("no reply to pop in Connection(%s)!", name));

        return replies.poll();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
