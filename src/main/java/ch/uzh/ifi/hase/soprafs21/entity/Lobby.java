package ch.uzh.ifi.hase.soprafs21.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.io.Serializable;

public class Lobby implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long lobbyId;

    @OneToOne(mappedBy = "Lobby")
    @Column(nullable = false, unique = true)
    private User owner;//User id

    @OneToOne(mappedBy = "Lobby")
    @Column(unique = true)
    private User player2;//User id

    @Column(nullable = false)
    private boolean player2Ready=false;//game can only be started if player 2 is ready



    public boolean isPlayer2Ready() {
        return player2Ready;
    }

    public void setPlayer2Ready(boolean player2Ready) {
        this.player2Ready = player2Ready;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getPlayer2() {
        return player2;
    }

    public void setPlayer2(User player2) {
        this.player2 = player2;
    }

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }
}
