package mlg.party.games.util;

import com.google.gson.Gson;
import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;
import mlg.party.lobby.websocket.requests.JoinLobbyRequest;
import mlg.party.lobby.websocket.responses.BasicWebSocketResponse;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class GameExecutor {
    public final TestWebSocketClient leader;
    public final List<TestWebSocketClient> players;
    private final Gson gson = new Gson();

    private final int TIMEOUT_S = 1;

    public GameExecutor(TestWebSocketClient leader, List<TestWebSocketClient> players) {
        this.leader = leader;
        this.players = players;
    }

    public List<String> sendPlayerMessage(BasicWebSocketRequest request) throws IOException {
        List<String> responses = new LinkedList<>();

        for (TestWebSocketClient player : players) {
            if (request instanceof JoinLobbyRequest)
                ((JoinLobbyRequest) request).setPlayerName(player.name);

            player.send(gson.toJson(request));
            await().atMost(TIMEOUT_S, TimeUnit.SECONDS).until(player.hasReplyCallable());
            responses.add(player.popReply());
        }

        return responses;
    }

    public List<List<String>> popAllPlayerResponses() {
        List<List<String>> replies = new LinkedList<>();

        for (TestWebSocketClient player : players)
            replies.add(popConnectionResponses(player));

        return replies;
    }

    public List<String> popAllLeaderResponses() {
        return popConnectionResponses(leader);
    }

    private List<String> popConnectionResponses(TestWebSocketClient client) {
        List<String> repliesPrime = new LinkedList<>();

        while (client.hasReply())
            repliesPrime.add(client.popReply());

        return repliesPrime;
    }


    public String sendLeaderMessage(BasicWebSocketRequest request) throws IOException {
        leader.send(gson.toJson(request));
        await().atMost(TIMEOUT_S, TimeUnit.SECONDS).until(leader.hasReplyCallable());
        return leader.popReply();
    }


}
