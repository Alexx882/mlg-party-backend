package mlg.party.games.rps;

import mlg.party.RequestParserBase;
import mlg.party.games.GameWebSocketHandler;
import mlg.party.games.rps.websocket.requests.RpsResult;
import mlg.party.lobby.logging.ILogger;
import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
public class RpsSocketHandler extends GameWebSocketHandler<RpsGame> {

    private final RequestParserBase requestParser;
    private final ILogger logger;

    public RpsSocketHandler(@Qualifier("RpsRequestParser") RequestParserBase requestParser, ILogger logger) {
        this.requestParser = requestParser;
        this.logger = logger;
    }

    @Override
    protected boolean handleRequest(WebSocketSession session, BasicWebSocketRequest request) throws IOException {
        if (!super.handleRequest(session, request) && request instanceof RpsResult) {
            handleRequest(session, (RpsResult) request);
            return true;
        }

        return false;
    }

    protected void handleRequest(WebSocketSession session, RpsResult request) {
        RpsGame res = gameInstances.get(request.lobbyId);
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
}
