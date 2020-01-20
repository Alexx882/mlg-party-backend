package mlg.party.games;

import com.google.gson.Gson;
import mlg.party.games.util.GameExecutor;
import mlg.party.games.util.TestWebSocketClient;
import mlg.party.lobby.websocket.requests.CreateLobbyRequest;
import mlg.party.lobby.websocket.requests.JoinLobbyRequest;
import mlg.party.lobby.websocket.requests.StartGameRequest;
import mlg.party.lobby.websocket.responses.JoinLobbyResponse;
import mlg.party.lobby.websocket.responses.LobbyCreatedResponse;
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
        String reply = executor.sendMessage(createLobbyRequest, true);

        LobbyCreatedResponse lobbyCreatedResponse = gson.fromJson(reply, LobbyCreatedResponse.class);

        assertEquals("CreateLobby", lobbyCreatedResponse.type);
    }


}
