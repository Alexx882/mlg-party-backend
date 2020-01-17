package mlg.party.lobby.games;

import mlg.party.lobby.lobby.Player;
import mlg.party.lobby.websocket.SocketHandler;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;

public class GameParameters {

    public GameParameters(String lobbyId, HashMap<Player, WebSocketSession> players, SocketHandler socketHandler) {
        this.lobbyId = lobbyId;
        this.players = players;
        this.socketHandler = socketHandler;
    }

    public String lobbyId;
    public HashMap<Player, WebSocketSession> players;
    public SocketHandler socketHandler;
}
