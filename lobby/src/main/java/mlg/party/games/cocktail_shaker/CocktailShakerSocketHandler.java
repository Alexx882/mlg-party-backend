package mlg.party.games.cocktail_shaker;

import mlg.party.games.GameWebSocketHandler;
import mlg.party.games.cocktail_shaker.websocket.RequestParser;
import mlg.party.games.cocktail_shaker.websocket.requests.CocktailShakerResult;
import mlg.party.lobby.websocket.IRequestParser;
import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class CocktailShakerSocketHandler extends GameWebSocketHandler<CocktailShakerGame> {

    @Override
    protected void handleRequest(WebSocketSession session, BasicWebSocketRequest request) {
        if (request instanceof CocktailShakerResult)
            handleRequest(session, (CocktailShakerResult)request);
        else
            System.out.println("Did not handle message");
    }

    protected void handleRequest(WebSocketSession session, CocktailShakerResult request) {
        CocktailShakerGame res = gameInstances.get(request.lobbyId);
        res.handleNewResult(request);
    }

    @Override
    protected IRequestParser getMessageParser() {
        return new RequestParser();
    }
}
