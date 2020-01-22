package mlg.party.games.quiz;

import mlg.party.games.GameWebSocketHandler;
import mlg.party.games.quiz.websocket.requests.QuizResult;
import mlg.party.lobby.logging.ILogger;
import mlg.party.RequestParserBase;
import mlg.party.lobby.websocket.LobbySocketHandler;
import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
public class QuizSocketHandler extends GameWebSocketHandler<QuizGame> {

    private final RequestParserBase requestParser;
    private final ILogger logger;
    private final LobbySocketHandler lobbySocketHandler;

    public QuizSocketHandler(@Qualifier("QuizRequestParser") RequestParserBase requestParser, ILogger logger, LobbySocketHandler lobbySocketHandler) {
        this.requestParser = requestParser;
        this.logger = logger;
        this.lobbySocketHandler = lobbySocketHandler;
    }

    @Override
    protected boolean handleRequest(WebSocketSession session, BasicWebSocketRequest request) throws IOException {
        if (!super.handleRequest(session, request) && request instanceof QuizResult) {
            handleRequest(session, (QuizResult) request);
            return true;
        }

        return false;
    }

    protected void handleRequest(WebSocketSession session, QuizResult request) {
        QuizGame res = gameInstances.get(request.lobbyId);
        res.handleNewResult(request);
    }

    @Override
    protected RequestParserBase getMessageParser() {
        return requestParser;
    }

    @Override
    protected ILogger getLogger() {
        return logger;
    }

    public void redirectToNextGame(QuizGame instance) {
        super.redirectToNextGame(instance, lobbySocketHandler);
    }
}
