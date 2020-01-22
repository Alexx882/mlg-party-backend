package mlg.party.games.tictactoe;

import mlg.party.RequestParserBase;
import mlg.party.games.BasicGame;
import mlg.party.games.GameFactory;
import mlg.party.games.GameWebSocketHandler;
import mlg.party.games.tictactoe.websocket.requests.TicTacToeMoveRequest;
import mlg.party.lobby.logging.ILogger;
import mlg.party.lobby.websocket.LobbySocketHandler;
import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;
import mlg.party.lobby.websocket.requests.StartGameRequest;
import mlg.party.lobby.websocket.responses.StartGameResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.persistence.Lob;
import java.io.IOException;

@Component
public class TicTacToeSocketHandler extends GameWebSocketHandler<TicTacToeGame> {
    private final RequestParserBase requestParser;
    private final ILogger logger;
    private final LobbySocketHandler lobbySocketHandler;

    public TicTacToeSocketHandler(@Qualifier("TicTacToeRequestParser") RequestParserBase requestParser, ILogger logger, LobbySocketHandler lobbySocketHandler) {
        this.requestParser = requestParser;
        this.logger = logger;
        this.lobbySocketHandler = lobbySocketHandler;
    }

    @Override
    protected boolean handleRequest(WebSocketSession session, BasicWebSocketRequest request) throws IOException {
        if (!super.handleRequest(session, request) && request instanceof TicTacToeMoveRequest) {
            handleRequest((TicTacToeMoveRequest) request);
            return true;
        }
        return false;
    }

    protected void handleRequest(TicTacToeMoveRequest request) {
        TicTacToeGame res = gameInstances.get(request.lobbyId);
        res.handleMoveRequest(request);
    }

    @Override
    protected RequestParserBase getMessageParser() {
        return requestParser;
    }

    @Override
    protected ILogger getLogger() {
        return logger;
    }

    public void redirectToNextGame(TicTacToeGame instance) {
        super.redirectToNextGame(instance, lobbySocketHandler);
    }
}
