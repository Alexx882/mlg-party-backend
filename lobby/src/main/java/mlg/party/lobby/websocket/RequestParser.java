package mlg.party.lobby.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import mlg.party.lobby.logging.ILogger;
import mlg.party.lobby.websocket.requests.AbstractWebsocketRequest;
import mlg.party.lobby.websocket.requests.CreateLobbyRequest;
import org.springframework.stereotype.Service;

@Service
public class RequestParser implements IRequestParser {

    private static final Gson gson = new Gson();
    private final ILogger logger;

    public RequestParser(ILogger logger) {
        this.logger = logger;
    }

    @Override
    public AbstractWebsocketRequest parseMessage(String json) {
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
                case "CreateLobby":
                    return gson.fromJson(json, CreateLobbyRequest.class);
                default:
                    throw new IllegalArgumentException("Invalid message type: '" + type + "'");
            }
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse Json");
        }
    }
}
