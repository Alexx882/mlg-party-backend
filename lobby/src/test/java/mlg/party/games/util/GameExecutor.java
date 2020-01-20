package mlg.party.games.util;

import com.google.gson.Gson;
import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;
import mlg.party.lobby.websocket.responses.BasicWebSocketResponse;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class GameExecutor {
    public final TestWebSocketClient leader;
    public final List<TestWebSocketClient> players;
    private final Gson gson = new Gson();

    public GameExecutor(TestWebSocketClient leader, List<TestWebSocketClient> players) {
        this.leader = leader;
        this.players = players;
    }

    public String sendMessage(BasicWebSocketRequest request, boolean isLeaderRequest) throws IOException {
        if (isLeaderRequest) {
            leader.send(gson.toJson(request));
            await().atMost(1, TimeUnit.SECONDS).until(leader.hasReplyCallable());
            return leader.popReply();
        }

        return null;
    }


}
