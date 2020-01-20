package mlg.party.games.rps.websocket.requests;

import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;

public class RpsResult extends BasicWebSocketRequest {

    public RpsResult(String lobbyId, String playerId, Option option) {
        super("RpsResult");
        this.lobbyId = lobbyId;
        this.playerId = playerId;
        this.option = option;
    }

    public String lobbyId;
    public String playerId;
    public enum Option {ROCK, PAPER, SCISSOR}
    public Option option;


}