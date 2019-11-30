package mlg.party.lobby.lobby;

import java.util.List;

public interface ILobbyService {

    /**
     * creates a new lobby and adds the given player into it
     *
     * @param player - holds all data of the requesting player
     * @return ID of the new lobby
     */
    String createLobby(Player player);

    /**
     * @param lobbyId
     * @param player
     * @return
     */
    boolean addPlayerToLobby(String lobbyId, Player player);

    List<Player> getPlayersForLobby(String lobbyId);
}
