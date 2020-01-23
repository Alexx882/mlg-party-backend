package mlg.party.lobby.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import mlg.party.RequestParserBase;
import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;
import mlg.party.lobby.websocket.requests.CreateLobbyRequest;
import mlg.party.lobby.websocket.requests.JoinLobbyRequest;
import mlg.party.lobby.websocket.requests.StartGameRequest;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
public class RequestParser extends RequestParserBase {

    private static final Gson gson = new Gson();

    @Override
    public BasicWebSocketRequest parseMessage(String json) {
        JsonObject jsonObject = super.getJsonWithTypeField(json);
        String type = jsonObject.get("type").getAsString();

        try {
            switch (type) {
                case "CreateLobby":
                    if (!jsonObject.has("playerName"))
                        throw new IllegalArgumentException("Missing fields");
                    return gson.fromJson(json, CreateLobbyRequest.class);

                case "JoinLobby":
                    if (!jsonObject.has("lobbyId") || !jsonObject.has("playerName"))
                        throw new IllegalArgumentException("Missing fields");
                    return gson.fromJson(json, JoinLobbyRequest.class);

                case "StartGame":
                    if (!jsonObject.has("lobbyId"))
                        throw new IllegalArgumentException("Missing fields");
                    return gson.fromJson(json, StartGameRequest.class);

                default:
                    throw new IllegalArgumentException("Invalid message type: '" + type + "'");
            }
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse Json");
        }
    }
}
