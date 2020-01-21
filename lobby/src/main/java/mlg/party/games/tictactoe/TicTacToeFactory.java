package mlg.party.games.tictactoe;

import mlg.party.games.GameFactory;
import mlg.party.lobby.lobby.Player;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.PostConstruct;
import java.util.List;


@Service
@Configuration
@EnableWebSocket
public class TicTacToeFactory extends GameFactory<TicTacToeGame> implements WebSocketConfigurer {

    @Value("${mlg.games.endpoints.base}")
    private String host;

    @Value("${mlg.games.endpoints.tictactoe}")
    private String url;

    public TicTacToeFactory(TicTacToeSocketHandler handler) {
        this.handler = handler;
    }

    private final TicTacToeSocketHandler handler;

    @PostConstruct
    public void register() {
        registerFactory(this);
    }

    @Override
    public TicTacToeGame createGame(String lobbyId, List<Player> players) {
        TicTacToeGame ticTacToeGame = new TicTacToeGame(lobbyId, players, url);
        ticTacToeGame.setSocketHandler(handler);
        return ticTacToeGame;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, url).setAllowedOrigins("*");
    }
}

