package mlg.party.games.quiz;
import mlg.party.games.GameFactory;
import mlg.party.games.quiz.QuizGame;
import mlg.party.games.quiz.QuizSocketHandler;
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
public class QuizFactory extends GameFactory<QuizGame> implements WebSocketConfigurer {

    @Value("${mlg.games.endpoints.base}")
    private String host;

    @Value("${mlg.games.endpoints.quiz}")
    private String url;

    public QuizFactory(QuizSocketHandler handler) {
        this.handler = handler;
    }

    private final QuizSocketHandler handler;


    public void register() {
        registerFactory(this);
    }

    @Override
    public QuizGame createGame(String lobbyId, List<Player> players) {
        QuizGame quizGame = new QuizGame(lobbyId, players, url);
        quizGame.setSocketHandler(handler);
        return quizGame;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, url).setAllowedOrigins("*");
    }
}
