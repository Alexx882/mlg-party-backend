package mlg.party.lobby.game;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private static GameManager instance;

    private GameManager(){}

    public static GameManager getInstance(){
        if(instance == null)
            instance = new GameManager();
        return instance;
    }

    public List<BasicGame> games = new ArrayList<BasicGame>(10);

    public void registerGame(BasicGame game){
        games.add(game);
    }

    public void startNextGame(){

    }
}
