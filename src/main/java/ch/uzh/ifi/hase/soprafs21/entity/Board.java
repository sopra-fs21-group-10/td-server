package ch.uzh.ifi.hase.soprafs21.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Board")
public class Board implements Serializable {
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

//    @Column(nullable = false)
//    private List<List<String>> field= Arrays.asList(Arrays.asList("x", "x"), Arrays.asList("y", "x"));// for now just a dummy,
//    // could also be another table with an attribute for each tile
//
//    public List<List<String>> getField() {
//        return field;
//    }
//
//    public void setField(List<List<String>> field) {
//        this.field = field;
//    }

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