package mlg.party.games.cocktail_shaker.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import mlg.party.games.cocktail_shaker.websocket.requests.CocktailShakerResult;
import mlg.party.RequestParserBase;
import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;
import org.springframework.stereotype.Service;

@Service(value = "CocktailShakerRequestParser")
public class RequestParser extends RequestParserBase {

    private static final Gson gson = new Gson();

    @Override
    public BasicWebSocketRequest parseMessage(String json) {
        JsonObject jsonObject = super.getJsonWithTypeField(json);
        String type = jsonObject.get("type").getAsString();

        try {
            switch (type) {
                case "CocktailShakerResult":
                    if (!jsonObject.has("playerId") || !jsonObject.has("max") || !jsonObject.has("avg"))
                        throw new IllegalArgumentException("Missing fields");
                    return gson.fromJson(json, CocktailShakerResult.class);

                default:
                    throw new IllegalArgumentException("Invalid message type: '" + type + "'");
            }
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse Json");
        }
    }
}
