package mlg.party.games.rps.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import mlg.party.RequestParserBase;
import mlg.party.games.rps.websocket.requests.RpsResult;
import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;
import org.springframework.stereotype.Service;

@Service(value = "RpsRequestParser")
public class RequestParserRps extends RequestParserBase {

    private static final Gson gson = new Gson();

    @Override
    public BasicWebSocketRequest parseMessage(String json) {
        JsonObject jsonObject = super.getJsonWithTypeField(json);
        String type = jsonObject.get("type").getAsString();

        try {
            switch (type) {
                case "RpsResult":
                    if (!jsonObject.has("playerId") || !jsonObject.has("option"))
                    throw new IllegalArgumentException("Missing fields");
                    return gson.fromJson(json, RpsResult.class);

                default:
                    throw new IllegalArgumentException("Invalid message type: '" + type + "'");
            }
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse Json");
        }
    }
}
