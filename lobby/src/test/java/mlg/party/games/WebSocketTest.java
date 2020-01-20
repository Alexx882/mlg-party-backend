package mlg.party.games;

import com.google.gson.Gson;
import mlg.party.games.util.GameExecutor;
import mlg.party.games.util.TestWebSocketClient;
import mlg.party.games.websocket.requests.HelloGameRequest;
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

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketTest {

    private final String uri = "ws://localhost:%d/lobby";
    private final String uriShaker = "ws://localhost:%d/game/shaker";
    private final int N_CLIENTS = 4;

    private final Gson gson = new Gson();

    @LocalServerPort
    private int port;

    private GameExecutor executor;

    @Before
    public void setup() throws IOException, DeploymentException {
        connectNClientsToURI(N_CLIENTS, URI.create(String.format(uri, port)));
    }

    private void connectNClientsToURI(int n, URI uri) throws IOException, DeploymentException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        List<TestWebSocketClient> players = new LinkedList<>();

        // 1. connect with all clients to the websocket
        for (int i = 0; i < n; i++) {
            TestWebSocketClient client = new TestWebSocketClient(String.format("JUnit Player %d", i + 1));
            players.add(client);
            container.connectToServer(client, uri);
        }

        TestWebSocketClient leader = new TestWebSocketClient("JUnit GameLeader");
        container.connectToServer(leader, uri);

        if (executor == null || !executor.isInitialized())
            executor = new GameExecutor(
                    leader,
                    players
            );
        else
            executor.reset(leader, players);
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
        // 1. create a lobby
        CreateLobbyRequest createLobbyRequest = new CreateLobbyRequest(executor.leader.name);
        String reply = executor.sendLeaderMessage(createLobbyRequest);

        LobbyCreatedResponse lobbyCreatedResponse = gson.fromJson(reply, LobbyCreatedResponse.class);
        assertEquals("CreateLobby", lobbyCreatedResponse.type);

        // 2. let the players join the lobby
        JoinLobbyRequest joinLobbyRequest = new JoinLobbyRequest(lobbyCreatedResponse.lobbyName, "");
        List<String> joinLobbyResponses = executor.sendPlayerMessage(joinLobbyRequest);

        for (String response : joinLobbyResponses) {
            JoinLobbyResponse joinLobbyResponse = gson.fromJson(response, JoinLobbyResponse.class);
            assertEquals("JoinLobby", joinLobbyResponse.type);
        }

        // 3.1 empty the response queue of all the leader which should be full of "PlayerJoined" notifications
        List<String> leaderJoinLobbyResponses = executor.popAllLeaderResponses();
        for (String response : leaderJoinLobbyResponses) {
            PlayerListResponse joinLobbyResponse = gson.fromJson(response, PlayerListResponse.class);
            assertEquals("PlayerJoined", joinLobbyResponse.type);
        }

        // 3.2 empty the response queue of all the players which should be full of "PlayerJoined" notifications (except the last one)
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

        // 4. start the game
        StartGameRequest startGameRequest = new StartGameRequest(lobbyCreatedResponse.lobbyName);
        String startGameResponseString = executor.sendLeaderMessage(startGameRequest);

        StartGameResponse startGameResponse = gson.fromJson(startGameResponseString, StartGameResponse.class);
        assertEquals("StartGame", startGameResponse.type);

        // 5. get the players responses which should be "StartGame" ones
        List<List<String>> playerStartGameResponses = executor.popAllPlayerResponses();
        for (List<String> responses : playerStartGameResponses) {
            for (String response : responses) {
                StartGameResponse startGameResponsePrime = gson.fromJson(response, StartGameResponse.class);
                assertEquals("StartGame", startGameResponsePrime.type);
            }
        }

        // 6. all connections should be closed right now as all clients should connect to the game specific endpoint
        assertTrue(executor.isLeaderConnectionClosed());
        assertTrue(executor.isConnectionOfAllPlayersClosed());
    }

    @Test
    public void cocktailShakerTest() throws IOException, DeploymentException {
        // 1. create a lobby
        CreateLobbyRequest createLobbyRequest = new CreateLobbyRequest(executor.leader.name);
        String reply = executor.sendLeaderMessage(createLobbyRequest);

        LobbyCreatedResponse lobbyCreatedResponse = gson.fromJson(reply, LobbyCreatedResponse.class);
        assertEquals("CreateLobby", lobbyCreatedResponse.type);

        // 2. let the players join the lobby
        JoinLobbyRequest joinLobbyRequest = new JoinLobbyRequest(lobbyCreatedResponse.lobbyName, "");
        List<String> joinLobbyResponses = executor.sendPlayerMessage(joinLobbyRequest);

        for (String response : joinLobbyResponses) {
            JoinLobbyResponse joinLobbyResponse = gson.fromJson(response, JoinLobbyResponse.class);
            assertEquals("JoinLobby", joinLobbyResponse.type);
        }

        // 3.1 empty the response queue of all the leader which should be full of "PlayerJoined" notifications
        List<String> leaderJoinLobbyResponses = executor.popAllLeaderResponses();
        for (String response : leaderJoinLobbyResponses) {
            PlayerListResponse joinLobbyResponse = gson.fromJson(response, PlayerListResponse.class);
            assertEquals("PlayerJoined", joinLobbyResponse.type);
        }

        // 3.2 empty the response queue of all the players which should be full of "PlayerJoined" notifications (except the last one)
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

        // 4. start the game
        StartGameRequest startGameRequest = new StartGameRequest(lobbyCreatedResponse.lobbyName);
        String startGameResponseString = executor.sendLeaderMessage(startGameRequest);

        StartGameResponse startGameResponse = gson.fromJson(startGameResponseString, StartGameResponse.class);
        assertEquals("StartGame", startGameResponse.type);

        // 5. get the players responses which should be "StartGame" ones
        List<List<String>> playerStartGameResponses = executor.popAllPlayerResponses();
        for (List<String> responses : playerStartGameResponses) {
            for (String response : responses) {
                StartGameResponse startGameResponsePrime = gson.fromJson(response, StartGameResponse.class);
                assertEquals("StartGame", startGameResponsePrime.type);
            }
        }

        // 6. all connections should be closed right now as all clients should connect to the game specific endpoint
        assertTrue(executor.isLeaderConnectionClosed());
        assertTrue(executor.isConnectionOfAllPlayersClosed());

        // 7. connect to the new service

        connectNClientsToURI(N_CLIENTS, URI.create(String.format(uriShaker, port)));

        // 8. identify yourself at the new service
//        HelloGameRequest request = new HelloGameRequest(executor.leader.name, lobbyCreatedResponse.lobbyName);
//        executor.sendLeaderMessage(request);
    }
}
