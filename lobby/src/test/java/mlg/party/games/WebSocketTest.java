package mlg.party.games;

import com.google.gson.Gson;
import mlg.party.games.util.GameExecutor;
import mlg.party.games.util.TestWebSocketClient;
import mlg.party.lobby.websocket.requests.CreateLobbyRequest;
import mlg.party.lobby.websocket.requests.JoinLobbyRequest;
import mlg.party.lobby.websocket.requests.StartGameRequest;
import mlg.party.lobby.websocket.responses.JoinLobbyResponse;
import mlg.party.lobby.websocket.responses.LobbyCreatedResponse;
import mlg.party.lobby.websocket.responses.PlayerListResponse;
import mlg.party.lobby.websocket.responses.StartGameResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketTest {

    private final String uri = "ws://localhost:%d/lobby";
    private final int N_CLIENTS = 4;

    private final Gson gson = new Gson();

    @LocalServerPort
    private int port;

    private GameExecutor executor;

    @Before
    public void setup() throws IOException, DeploymentException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        List<TestWebSocketClient> players = new LinkedList<>();

        // 1. connect with all clients to the websocket
        for (int i = 0; i < N_CLIENTS; i++) {
            TestWebSocketClient client = new TestWebSocketClient(String.format("JUnit Player %d", i + 1));
            players.add(client);
            container.connectToServer(client, URI.create(String.format(uri, port)));
        }

        TestWebSocketClient leader = new TestWebSocketClient("JUnit GameLeader");
        container.connectToServer(leader, URI.create(String.format(uri, port)));

        executor = new GameExecutor(
                leader,
                players
        );
    }

    @Test
    public void createLobbyTest() throws Exception {
        CreateLobbyRequest createLobbyRequest = new CreateLobbyRequest(executor.leader.name);
        String reply = executor.sendLeaderMessage(createLobbyRequest);

        LobbyCreatedResponse lobbyCreatedResponse = gson.fromJson(reply, LobbyCreatedResponse.class);

        assertEquals("CreateLobby", lobbyCreatedResponse.type);
    }

    @Test
    public void joinLobbyTest() throws Exception {
        CreateLobbyRequest createLobbyRequest = new CreateLobbyRequest(executor.leader.name);
        String reply = executor.sendLeaderMessage(createLobbyRequest);

        LobbyCreatedResponse lobbyCreatedResponse = gson.fromJson(reply, LobbyCreatedResponse.class);

        assertEquals("CreateLobby", lobbyCreatedResponse.type);

        JoinLobbyRequest joinLobbyRequest = new JoinLobbyRequest(lobbyCreatedResponse.lobbyName, "");
        List<String> responses = executor.sendPlayerMessage(joinLobbyRequest);

        for (String response : responses) {
            JoinLobbyResponse joinLobbyResponse = gson.fromJson(response, JoinLobbyResponse.class);
            assertEquals("JoinLobby", joinLobbyResponse.type);
        }
    }

    @Test
    public void startGameTest() throws IOException {
        CreateLobbyRequest createLobbyRequest = new CreateLobbyRequest(executor.leader.name);
        String reply = executor.sendLeaderMessage(createLobbyRequest);

        LobbyCreatedResponse lobbyCreatedResponse = gson.fromJson(reply, LobbyCreatedResponse.class);

        assertEquals("CreateLobby", lobbyCreatedResponse.type);

        JoinLobbyRequest joinLobbyRequest = new JoinLobbyRequest(lobbyCreatedResponse.lobbyName, "");
        List<String> joinLobbyResponses = executor.sendPlayerMessage(joinLobbyRequest);

        for (String response : joinLobbyResponses) {
            JoinLobbyResponse joinLobbyResponse = gson.fromJson(response, JoinLobbyResponse.class);
            assertEquals("JoinLobby", joinLobbyResponse.type);
        }

        List<String> leaderJoinLobbyResponses = executor.popAllLeaderResponses();
        for (String response : leaderJoinLobbyResponses) {
            PlayerListResponse joinLobbyResponse = gson.fromJson(response, PlayerListResponse.class);
            assertEquals("PlayerJoined", joinLobbyResponse.type);
        }

        boolean emptyResponseQueue = true;
        List<List<String>> playerJoinLobbyResponses = executor.popAllPlayerResponses();
        for (List<String> responses : playerJoinLobbyResponses) {
            if (responses.isEmpty()) {
                if (emptyResponseQueue)
                    emptyResponseQueue = false;
                else
                    fail("only the last player has an empty response queue after everyone joined the lobby");
            }

            for (String response : responses) {
                JoinLobbyResponse joinLobbyResponse = gson.fromJson(response, JoinLobbyResponse.class);
                assertEquals("PlayerJoined", joinLobbyResponse.type);
            }
        }

        StartGameRequest startGameRequest = new StartGameRequest(lobbyCreatedResponse.lobbyName);
        String startGameResponseString = executor.sendLeaderMessage(startGameRequest);

        StartGameResponse startGameResponse = gson.fromJson(startGameResponseString, StartGameResponse.class);
        assertEquals("StartGame", startGameResponse.type);

        List<List<String>> playerStartGameResponses = executor.popAllPlayerResponses();
        for (List<String> responses : playerStartGameResponses) {
            for (String response : responses) {
                StartGameResponse startGameResponsePrime = gson.fromJson(response, StartGameResponse.class);
                assertEquals("StartGame", startGameResponsePrime.type);
            }
        }
    }
}
