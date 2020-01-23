package mlg.party.games.spacepirates.websocket;


import mlg.party.RequestParserBase;
import mlg.party.games.GameWebSocketHandler;
import mlg.party.games.spacepirates.SpacePiratesGame;
import mlg.party.games.spacepirates.websocket.requests.SpacePiratesResult;
import mlg.party.lobby.logging.ILogger;
import mlg.party.lobby.websocket.LobbySocketHandler;
import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
public class SpacePiratesSocketHandler extends GameWebSocketHandler<SpacePiratesGame> {

    private final RequestParserBase requestParser;
    private final ILogger logger;
    private final LobbySocketHandler lobbySocketHandler;

    public SpacePiratesSocketHandler(@Qualifier("SpacePiratesRequestParser") RequestParserBase requestParser, ILogger logger, LobbySocketHandler lobbySocketHandler) {
        this.requestParser = requestParser;
        this.logger = logger;
        this.lobbySocketHandler = lobbySocketHandler;
    }

    @Override
    protected boolean handleRequest(WebSocketSession session, BasicWebSocketRequest request) throws IOException {
        if (!super.handleRequest(session, request) && request instanceof SpacePiratesResult) {
            handleRequest(session, (SpacePiratesResult) request);
            return true;
        }

        return false;
    }

    protected void handleRequest(WebSocketSession session, SpacePiratesResult request) {
        SpacePiratesGame res = gameInstances.get(request.lobbyId);
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

    public void redirectToNextGame(SpacePiratesGame instance) {
        super.redirectToNextGame(instance, lobbySocketHandler);
    }
}
