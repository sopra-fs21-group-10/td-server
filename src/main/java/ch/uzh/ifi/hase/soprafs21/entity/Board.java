package ch.uzh.ifi.hase.soprafs21.entity;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "BOARD")
public class Board implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long boardId;

    @OneToOne
    private User owner;

    @Column(nullable = false)
    private int health = 50;

    @Column(nullable = false)
    private int gold = 500;

    @Column(nullable = false)
    private String weather;

    private static final String BLOCKED = "blocked"; // the path where the minions walk through

    @Column(nullable = false, length = 1000)// caused error, maybe increase even more
    private String[][] gameMap = {//10*15
            {null, BLOCKED, null ,null, null, null, null,null, null, null ,null, null, null, null, null},
            {null, BLOCKED, null ,null, null, null, null,null, null, null ,null, null, null, null, null},
            {null, BLOCKED, BLOCKED ,BLOCKED, BLOCKED, BLOCKED, BLOCKED,BLOCKED, BLOCKED, BLOCKED ,BLOCKED, BLOCKED, BLOCKED, BLOCKED, null},
            {null, null, null ,null, null, null, null,null, null, null ,null, null, null, BLOCKED, null},
            {null, null, null ,null, null, null, BLOCKED,BLOCKED, BLOCKED, BLOCKED ,BLOCKED, BLOCKED, BLOCKED, BLOCKED, null},
            {null, null, null ,null, null, BLOCKED, BLOCKED,null, null, null ,null, null, null, null, null},
            {null, BLOCKED, BLOCKED ,BLOCKED, BLOCKED, BLOCKED, null,null, null, null ,null, null, null, null, null},
            {null, BLOCKED, null ,null, null, null, null,null, null, null ,null, null, null, null, null},
            {null, BLOCKED, BLOCKED ,BLOCKED, BLOCKED, BLOCKED, BLOCKED,BLOCKED, BLOCKED, BLOCKED ,null, null, null, null, null},
            {null, null, null ,null, null, null, null,null, null, BLOCKED ,null, null, null, null, null}
    };

    @ElementCollection // 1
    @CollectionTable(name = "minions", joinColumns = @JoinColumn(name = "id")) // 2
    @Column(name = "minions")
    private List<String> minions = new ArrayList<>();

    public List<String> getMinions() {
        return minions;
    }

    public void setMinions(List<String> minions) {
        this.minions = minions;
    }

    public String[][] getGameMap() {
        return gameMap;
    }

    public void setGameMap(String[][] gameMap) {
        this.gameMap = gameMap;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }// theoretically not save because could show every string

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

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(long boardId) {
        this.boardId = boardId;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}