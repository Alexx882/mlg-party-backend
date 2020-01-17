package mlg.party.lobby.games;

import com.google.gson.Gson;
import mlg.party.lobby.websocket.requests.CreateLobbyRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.net.URI;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebSocketTest {

    private final String uri = "ws://localhost:%d/lobby";

    private final Gson gson = new Gson();

    @LocalServerPort
    private int port;

    @Before
    public void setup() {
    }

    @Test
    public void createSessionAfterOpenLogWebSocketHandler() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        TestWebSocketClient client = new TestWebSocketClient();

        container.connectToServer(client, URI.create(String.format(uri, port)));

        CreateLobbyRequest request = new CreateLobbyRequest("JUnit");
        client.send(gson.toJson(request));
    }

}
