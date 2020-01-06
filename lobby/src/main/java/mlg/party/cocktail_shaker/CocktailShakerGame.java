package mlg.party.cocktail_shaker;

import mlg.party.Callback;
import mlg.party.lobby.games.BasicGame;
import mlg.party.lobby.games.GameFinishedArgs;
import mlg.party.lobby.games.GameParameters;
import mlg.party.lobby.lobby.Player;
import mlg.party.lobby.websocket.responses.StartGameResponse;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

public class CocktailShakerGame extends BasicGame {

    @Override
    public void startGame() {

        // inform players
        StartGameResponse response = new StartGameResponse(200, getGameName());

        for (Map.Entry<Player, WebSocketSession> entry : players.entrySet()) {
            try {
                socketHandler.sendMessage(entry.getValue(), response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void registerResultCallback(Callback<GameFinishedArgs> callback) {

    }

    @Override
    public String getGameName() {
        return "CocktailShaker";
    }
}
