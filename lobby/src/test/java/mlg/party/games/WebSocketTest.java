package mlg.party.games;

import com.google.gson.Gson;
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
import javax.websocket.WebSocketContainer;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketTest {

    private final String uri = "ws://localhost:%d/lobby";
    private final int N_CLIENTS = 4;

    private final Gson gson = new Gson();

    @LocalServerPort
    private int port;

    @Before
    public void setup() {
    }

    @Test
    public void createSessionAfterOpenLogWebSocketHandler() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        List<TestWebSocketClient> clients = new LinkedList<>();

        // 1. connect with all clients to the websocket
        for (int i = 0; i < N_CLIENTS; i++) {
            TestWebSocketClient client = new TestWebSocketClient(String.format("JUnit Player %d", i + 1));
            clients.add(client);
            container.connectToServer(client, URI.create(String.format(uri, port)));
        }

        // 2. first one will be the lobby leader
        TestWebSocketClient leader = clients.get(0);

        // 3. the leader creates a lobby and receives it's ID
        CreateLobbyRequest request = new CreateLobbyRequest(leader.name);
        leader.send(gson.toJson(request));
        leader.waitForResponse();

        LobbyCreatedResponse lobbyCreatedResponse = gson.fromJson(leader.getReplies().poll(), LobbyCreatedResponse.class);

        // 4. the others join the lobby
        for (TestWebSocketClient client : clients) {
            if (client != leader) {
                JoinLobbyRequest joinLobbyRequest = new JoinLobbyRequest(lobbyCreatedResponse.getLobbyName(), client.name);
                client.send(gson.toJson(joinLobbyRequest));
                client.waitForResponse();

                JoinLobbyResponse joinLobbyResponse = gson.fromJson(client.getReplies().poll(), JoinLobbyResponse.class);
            }
        }

        // 5. the leader starts the game
        StartGameRequest startGameRequest = new StartGameRequest(lobbyCreatedResponse.getLobbyName());
        leader.send(gson.toJson(startGameRequest));

        for(TestWebSocketClient client : clients)
            client.waitForResponse();


    }

}
