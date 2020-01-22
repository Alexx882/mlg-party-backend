package mlg.party.games.tictactoe.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import mlg.party.RequestParserBase;
import mlg.party.games.cocktail_shaker.websocket.requests.CocktailShakerResult;
import mlg.party.games.tictactoe.websocket.requests.TicTacToeMoveRequest;
import mlg.party.games.websocket.requests.HelloGameRequest;
import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;
import org.springframework.stereotype.Service;

@Service(value = "TicTacToeRequestParser")
public class TicTacToeRequestParser extends RequestParserBase {

    private static final Gson gson = new Gson();

    @Override
    public BasicWebSocketRequest parseMessage(String json) {
        JsonObject jsonObject = super.getJsonWithTypeField(json);
        String type = jsonObject.get("type").getAsString();
        System.out.println(jsonObject);
        try {
            switch (type) {
                case "HelloGame":
                    if (!jsonObject.has("playerId") || !jsonObject.has("lobbyName"))
                        throw new IllegalArgumentException("Missing fields");
                    return gson.fromJson(json, HelloGameRequest.class);
                case "TicTacToeMove":
                    if (!jsonObject.has("playerId") ||!jsonObject.has("lobbyId")|| !jsonObject.has("x") || !jsonObject.has("y"))
                        throw new IllegalArgumentException("Missing fields");
                    return gson.fromJson(json, TicTacToeMoveRequest.class);
                default:
                    throw new IllegalArgumentException("Invalid message type: '" + type + "'");
            }
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse Json");
        }
    }
}
