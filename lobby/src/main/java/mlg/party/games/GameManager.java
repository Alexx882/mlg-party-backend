package mlg.party.games;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GameManager {

    private List<GameFactory<?>> games = new ArrayList<>(10);

    public BasicGame getNextGame() {
        Collections.shuffle(games);
        return games.get(0).createGame();
    }
}
