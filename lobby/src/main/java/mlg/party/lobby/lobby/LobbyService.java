package mlg.party.lobby.lobby;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Service
public class LobbyService implements ILobbyService {

    private int lobbyNameLength = 4;
    private char[] alphabet = "1234567890".toCharArray();

    private List<String> sessions = Collections.synchronizedList(new LinkedList<>());
    private final Random rng = new Random();

    @Override
    public String createLobby(String id) {
        StringBuilder sb = new StringBuilder();

        do {
            for (int i = 0; i < lobbyNameLength; i++)
                sb.append(alphabet[rng.nextInt(alphabet.length)]);
        } while (sessions.contains(sb.toString()));

        String lobbyName = sb.toString();

        sessions.add(lobbyName);
        return lobbyName;
    }
}
