package ch.uzh.ifi.hase.soprafs21.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "TOWER")
public class Tower implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long towerId;

    @Column(nullable = false, unique = true)
    private String towerName;

    @Column(nullable = false)
    private int cost = 0;

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
    public String getTowerName() {
        return towerName;
    }

    public void setTowerName(String towerName) {
        this.towerName = towerName;
    }

    public Long getTowerId() {
        return towerId;
    }

    public void setTowerId(Long towerId) {
        this.towerId = towerId;
    }

}
