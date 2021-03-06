package mlg.party.games.clicker.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import mlg.party.RequestParserBase;
import mlg.party.games.clicker.websocket.requests.ClickerResult;
import mlg.party.games.websocket.requests.HelloGameRequest;
import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;
import org.springframework.stereotype.Service;

@Service(value = "ClickerRequestParser")
public class RequestParser extends RequestParserBase {

    private static final Gson gson = new Gson();

    @Override
    public BasicWebSocketRequest parseMessage(String json) {
        JsonObject jsonObject = super.getJsonWithTypeField(json);
        String type = jsonObject.get("type").getAsString();

        try {
            switch (type) {
                case "HelloGame":
                    if (!jsonObject.has("playerId") || !jsonObject.has("lobbyId"))
                        throw new IllegalArgumentException("Missing fields");
                    return gson.fromJson(json, HelloGameRequest.class);
                case "ClickerResults":
                    if (!jsonObject.has("playerId") || !jsonObject.has("max"))
                        throw new IllegalArgumentException("Missing fields");
                    return gson.fromJson(json, ClickerResult.class);

                default:
                    throw new IllegalArgumentException("Invalid message type: '" + type + "'");
            }
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse Json");
        }
    }
}
