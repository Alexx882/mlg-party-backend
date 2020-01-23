package mlg.party.games.cocktail_shaker.websocket;

import mlg.party.games.GameWebSocketHandler;
import mlg.party.games.cocktail_shaker.CocktailShakerGame;
import mlg.party.games.cocktail_shaker.websocket.requests.CocktailShakerResult;
import mlg.party.lobby.logging.ILogger;
import mlg.party.RequestParserBase;
import mlg.party.lobby.websocket.LobbySocketHandler;
import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
public class CocktailShakerSocketHandler extends GameWebSocketHandler<CocktailShakerGame> {

    private final RequestParserBase requestParser;
    private final ILogger logger;
    private final LobbySocketHandler lobbySocketHandler;

    public CocktailShakerSocketHandler(@Qualifier("CocktailShakerRequestParser") RequestParserBase requestParser, ILogger logger, LobbySocketHandler lobbySocketHandler) {
        this.requestParser = requestParser;
        this.logger = logger;
        this.lobbySocketHandler = lobbySocketHandler;
    }

    @Override
    protected boolean handleRequest(WebSocketSession session, BasicWebSocketRequest request) throws IOException {
        if (!super.handleRequest(session, request) && request instanceof CocktailShakerResult) {
            handleRequest(session, (CocktailShakerResult) request);
            return true;
        }

        return false;
    }

    protected void handleRequest(WebSocketSession session, CocktailShakerResult request) {
        gameInstances.forEach((k,v) -> System.out.println(k + ":" + v));
        CocktailShakerGame res = gameInstances.get(request.lobbyId);
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

    public void redirectToNextGame(CocktailShakerGame instance) {
        super.redirectToNextGame(instance, lobbySocketHandler);
    }
}
