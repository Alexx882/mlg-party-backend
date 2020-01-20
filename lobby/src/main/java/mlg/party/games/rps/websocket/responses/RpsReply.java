package mlg.party.games.rps.websocket.responses;

import mlg.party.games.rps.RpsLogic;
import mlg.party.games.rps.websocket.requests.RpsResult;
import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;

public class RpsReply extends BasicWebSocketRequest {

    public RpsReply(String lobbyId, RpsLogic.Option player1, RpsLogic.Option player2) {
        super("RpsResult");
        this.lobbyId = lobbyId;
        this.player1 = player1;
        this.player2 = player2;
    }

    public String lobbyId;
    public String playerId;
    public RpsLogic.Option player1;
    public RpsLogic.Option player2;


}