package mlg.party.lobby.websocket;

import mlg.party.lobby.logging.ConsoleLogger;
import mlg.party.lobby.websocket.requests.AbstractWebsocketRequest;
import mlg.party.lobby.websocket.requests.CreateLobbyRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ParserTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public IRequestParser requestParser() {
            return new RequestParser(new ConsoleLogger());
        }
    }

    @Autowired
    private RequestParser parser;

    @Test
    public void testProperlyFormattedCreateLobbyRequest() {
        String properlyFormattedCreateLobbyRequest = "{\"type\":\"CreateLobby\",\"playerName\":\"Franz\"}";
        AbstractWebsocketRequest request = parser.parseMessage(properlyFormattedCreateLobbyRequest);

        assert request instanceof CreateLobbyRequest;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingNameCreateRequest() {
        String properlyFormattedCreateLobbyRequest = "{\"type\":\"CreateLobby\"}";
        parser.parseMessage(properlyFormattedCreateLobbyRequest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInroperlyFormattedCreateLobbyRequest1() {
        String inProperlyFormattedCreateLobbyRequest1 = "{}";
        parser.parseMessage(inProperlyFormattedCreateLobbyRequest1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void noJsonArgumentString() {
        String noJsonString = "i am not a json String";
        parser.parseMessage(noJsonString);
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongMessageTypeException() {
        String properlyFormattedCreateLobbyRequest = "{\"type\":\"CreateLobbyyyyyyy\"}";
        parser.parseMessage(properlyFormattedCreateLobbyRequest);
    }

}
