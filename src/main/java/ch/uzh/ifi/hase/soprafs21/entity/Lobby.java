package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.PlayerLobbyStatus;

import javax.persistence.*;
import java.io.Serializable;
@Entity
public class Lobby implements Serializable {
    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue
    private Long lobbyId;
    @OneToOne
    private User owner;
    @OneToOne
    private User player2;
    @Enumerated
    private PlayerLobbyStatus playerLobbyStatus;

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(long lobbyId) {
        this.lobbyId= lobbyId;
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

    public PlayerLobbyStatus getLobbyStatus() {
        return playerLobbyStatus;
    }

    public void setLobbyStatus(PlayerLobbyStatus playerLobbyStatus) {
        this.playerLobbyStatus = playerLobbyStatus;
    }
}


