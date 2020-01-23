package mlg.party.games.clicker;


import mlg.party.games.GameWebSocketHandler;
import mlg.party.games.clicker.ClickerGame;
import mlg.party.games.clicker.websocket.requests.ClickerResult;
import mlg.party.lobby.logging.ILogger;
import mlg.party.RequestParserBase;
import mlg.party.lobby.websocket.LobbySocketHandler;
import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
public class ClickerSocketHandler extends GameWebSocketHandler<ClickerGame> {

    private final RequestParserBase requestParser;
    private final ILogger logger;
    private final LobbySocketHandler lobbySocketHandler;

    public ClickerSocketHandler(@Qualifier("ClickerRequestParser") RequestParserBase requestParser, ILogger logger, LobbySocketHandler lobbySocketHandler) {
        this.requestParser = requestParser;
        this.logger = logger;
        this.lobbySocketHandler = lobbySocketHandler;
    }

    @Override
    protected boolean handleRequest(WebSocketSession session, BasicWebSocketRequest request) throws IOException {
        if (!super.handleRequest(session, request) && request instanceof ClickerResult) {
            handleRequest(session, (ClickerResult) request);
            return true;
        }

        return false;
    }

    protected void handleRequest(WebSocketSession session, ClickerResult request) {
        ClickerGame res = gameInstances.get(request.lobbyId);
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

    public void redirectToNextGame(ClickerGame instance) {
        super.redirectToNextGame(instance, lobbySocketHandler);
    }
}
