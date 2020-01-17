package mlg.party.games.cocktail_shaker.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import mlg.party.games.cocktail_shaker.websocket.requests.CocktailShakerResult;
import mlg.party.lobby.websocket.IRequestParser;
import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;
import org.springframework.stereotype.Service;

public class RequestParser implements IRequestParser {

    private static final Gson gson = new Gson();

    @Override
    public BasicWebSocketRequest parseMessage(String json) {
        // todo extract
        JsonObject jsonObject;

        try {
            jsonObject = JsonParser.parseString(json).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Passed String is not in JSON format!");
        }

        if (!jsonObject.has("type"))
            throw new IllegalArgumentException("No message type is contained in the String!");

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
