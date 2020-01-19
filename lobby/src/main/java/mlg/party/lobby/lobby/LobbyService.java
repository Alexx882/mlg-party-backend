package mlg.party.lobby.lobby;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

@Service
public class LobbyService implements ILobbyService {

    private int lobbyNameLength = 4;
    private char[] alphabet = "1234567890".toCharArray();

    private Map<String, List<Player>> lobbies = new HashMap<>();

    private final SecureRandom rng = new SecureRandom();

    /**
     * adds a new player to the passed lobby
     *
     * @param lobbyId - unique ID for the lobby
     * @param player  - holds all data concerning the new player
     * @return true iff the player was successful added to the lobby
     */
    @Override
    public boolean addPlayerToLobby(String lobbyId, Player player) {
        if (lobbies.containsKey(lobbyId)) {
            if (!lobbies.get(lobbyId).contains(player)) {
                lobbies.get(lobbyId).add(player);
                return true;
            }
        }

        return false;
    }

    @Override
    public List<Player> getPlayersForLobby(String lobbyId) {
        if (!lobbies.containsKey(lobbyId))
            throw new IllegalArgumentException(String.format("Lobby with ID '%s' does not exist!", lobbyId));

        return lobbies.get(lobbyId);
    }

    @Override
    public void closeLobby(String lobbyId) {
        if (!lobbies.containsKey(lobbyId))
            throw new IllegalArgumentException(String.format("Lobby with ID '%s' does not exist!", lobbyId));

        lobbies.remove(lobbyId);
    }

    /**
     * creates a new lobby and adds the requesting player to it
     *
     * @param player - holds all data of the requesting player
     * @return ID of the new lobby
     */
    @Override
    public String createLobby(Player player) {
        StringBuilder sb = new StringBuilder();

        do {
            for (int i = 0; i < lobbyNameLength; i++)
                sb.append(alphabet[rng.nextInt(alphabet.length)]);
        } while (lobbies.containsKey(sb.toString()));

        String lobbyName = sb.toString();

        lobbies.put(lobbyName, new LinkedList<>());
        lobbies.get(lobbyName).add(player);

        return lobbyName;
    }
}
