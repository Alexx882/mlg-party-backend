package mlg.party.lobby.lobby;

public interface ILobbyService {

    /**
     * creates a new lobby and adds the given player into it
     *
     * @param id - ID of the requesting player
     * @return ID of the new lobby
     */
    String createLobby(String id);
}
