package mlg.party.games.util;

import com.google.gson.Gson;
import mlg.party.games.cocktail_shaker.websocket.requests.CocktailShakerResult;
import mlg.party.games.websocket.requests.HelloGameRequest;
import mlg.party.lobby.lobby.Player;
import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;
import mlg.party.lobby.websocket.requests.JoinLobbyRequest;
import mlg.party.lobby.websocket.responses.BasicWebSocketResponse;

import javax.websocket.CloseReason;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class GameExecutor {
    public TestWebSocketClient leader;
    public List<TestWebSocketClient> players;
    private final Gson gson = new Gson();

    private final int TIMEOUT_S = 2;
    private final SecureRandom srng = new SecureRandom();

    public GameExecutor(TestWebSocketClient leader, List<TestWebSocketClient> players) {
        this.leader = leader;
        this.players = players;
    }

    public boolean isInitialized() {
        return leader != null && players.size() > 0;
    }

    public void close() throws IOException {
        if (leader.session.isOpen())
            leader.session.close();

        for (TestWebSocketClient player : players)
            if (player.session.isOpen())
                player.session.close();
    }

    public void reset(TestWebSocketClient leader, List<TestWebSocketClient> players) {
        this.leader = leader;
        this.players = players;
    }

    public void assignId(String id, int position) {
        players.get(position).setId(id);
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

    public String sendLeaderMessage(BasicWebSocketRequest request, boolean expectingAnswer) throws IOException {
        leader.send(gson.toJson(request));
        if (expectingAnswer) {
            await().atMost(TIMEOUT_S, TimeUnit.SECONDS).until(leader.hasReplyCallable());
            return leader.popReply();
        }

        return null;
    }

    public String sendLeaderMessage(BasicWebSocketRequest request) throws IOException {
        return sendLeaderMessage(request, true);
    }

    public List<String> sendPlayerMessage(BasicWebSocketRequest request) throws IOException {
        return sendPlayerMessage(request, true);
    }

    public List<String> sendPlayerMessage(BasicWebSocketRequest request, boolean expectingAnswer) throws IOException {
        List<String> responses = new LinkedList<>();

        for (TestWebSocketClient player : players) {
            if (request instanceof JoinLobbyRequest)
                ((JoinLobbyRequest) request).setPlayerName(player.name);
            else if (request instanceof HelloGameRequest)
                ((HelloGameRequest) request).setPlayerId(player.getId());
            else if (request instanceof CocktailShakerResult) {
                ((CocktailShakerResult) request).setPlayerId(player.getId());
                float result = srng.nextFloat();
                ((CocktailShakerResult) request).setAvg(result);
                ((CocktailShakerResult) request).setMax(result + 1);
            }

            player.send(gson.toJson(request));
            if (expectingAnswer) {
                await().atMost(TIMEOUT_S, TimeUnit.SECONDS).until(player.hasReplyCallable());
                responses.add(player.popReply());
            }
        }

        if (expectingAnswer)
            return responses;
        else
            return null;
    }

    public boolean isLeaderConnectionClosed() {
        return !leader.session.isOpen();
    }

    public boolean isConnectionOfAllPlayersClosed() {
        boolean allConnectionsClosed = true;

        for (TestWebSocketClient player : players) {
            allConnectionsClosed = allConnectionsClosed && !player.session.isOpen();
        }

        return allConnectionsClosed;
    }


}
