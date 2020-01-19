package mlg.party;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import mlg.party.lobby.websocket.requests.BasicWebSocketRequest;

public abstract class RequestParserBase {

    public JsonObject getJsonWithTypeField(String json)
            throws IllegalArgumentException {
        JsonObject jsonObject;

        IllegalArgumentException noJsonInputArgumentException = new IllegalArgumentException("Passed String is not in JSON format!");

        if (json == null)
            throw noJsonInputArgumentException;

        try {
            jsonObject = JsonParser.parseString(json).getAsJsonObject();
        } catch (JsonSyntaxException | IllegalStateException e) {
            throw noJsonInputArgumentException;
        }

        if (!jsonObject.has("type"))
            throw new IllegalArgumentException("No message type is contained in the String!");

        return jsonObject;
    }

    public abstract BasicWebSocketRequest parseMessage(String json);

}
