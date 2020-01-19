package mlg.party.games;

import mlg.party.lobby.lobby.Player;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class GameTestsHelper {
    /**
     * Returns three players with ids 1-3 and names P1-P3.
     * @return
     */
    public static List<Player> getThreeArbitraryPlayers() {
        var participants = new ArrayList<Player>(3);
        participants.add(new Player("1", "P1"));
        participants.add(new Player("2", "P2"));
        participants.add(new Player("3", "P3"));
        return participants;
    }

    @Test
    public void getThreeArbitraryPlayers_three(){
        var participants = getThreeArbitraryPlayers();
        Assert.assertNotNull(participants);
        Assert.assertEquals(3, participants.size());
    }
}
