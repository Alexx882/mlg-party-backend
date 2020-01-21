package mlg.party.games.rps.websocket.requests;

import mlg.party.games.rps.RpsLogic;
import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;

import java.security.SecureRandom;

public class RpsResult extends BasicWebSocketRequest {

    public RpsResult(String lobbyId, String playerId, String option) {
        super("RpsResult");
        this.lobbyId = lobbyId;
        this.playerId = playerId;
        this.option = this.option.valueOf(option);
    }

    public String lobbyId;
    public String playerId;
    public RpsLogic.Option option;


}