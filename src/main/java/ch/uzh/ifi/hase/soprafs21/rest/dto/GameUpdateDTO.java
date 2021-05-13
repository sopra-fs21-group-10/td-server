package ch.uzh.ifi.hase.soprafs21.rest.dto;

public class GameUpdateDTO {
    private int gold;
    private  int health;

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
