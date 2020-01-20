package mlg.party.games.rps.websocket.responses;

import mlg.party.games.rps.websocket.requests.RpsResult;
import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;

public class RpsReply extends BasicWebSocketRequest {

    public RpsReply(String lobbyId, RpsResult.Option player1, RpsResult.Option player2) {
        super("RpsResult");
        this.lobbyId = lobbyId;
        this.player1 = player1;
        this.player2 = player2;
    }

    public String lobbyId;
    public String playerId;
    public RpsResult.Option player1;
    public RpsResult.Option player2;


}