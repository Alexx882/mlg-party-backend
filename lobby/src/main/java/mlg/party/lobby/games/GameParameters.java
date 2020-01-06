package mlg.party.lobby.games;

import mlg.party.lobby.lobby.Player;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;

public class GameParameters {

    public String lobbyId;
    public List<Player> players;
    public TextWebSocketHandler socketHandler;

}
