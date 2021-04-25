package ch.uzh.ifi.hase.soprafs21.entity;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

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
    private int gold = 100;

    @Column(nullable = false)
    private String weather;

    private String blocked = "blocked";

    @Column(nullable = false)
    private String[][] board = {{null, blocked, null ,null, null, null, null},
            {null, blocked, null ,null, null, null, null},
            {null, blocked, blocked, null, null, null, null},
            {null, null, blocked, null, null, null, null},
            {null, null, blocked, null, null, null, null},
            {null, null, blocked, blocked, null, null, null},
            {null, null, null , blocked, null, null, null}};
    // for now just a dummy, could also be another entity  with a one to many relationship

    public String[][] getBoard() {
        return board;
    }

    public void setBoard(String[][] board) {
        this.board = board;
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