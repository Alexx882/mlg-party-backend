package mlg.party.lobby.lobby;

public class Player {
    private String id;
    private String name;
    private int points = 0;

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        String pName = name != null ? name : "undefined";
        return "Player(" + id + ", " + pName + ')';
    }

    public void increasePoints() {
        points++;
    }

    public int getPoints() {
        return points;
    }
}
