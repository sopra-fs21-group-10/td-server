package ch.uzh.ifi.hase.soprafs21.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Game")
public class Game implements Serializable {
    /*
    making a lot of optional variables for player 2 health, gold.... seems very bad
    I decided to split it up in two entities so if there is a player 2, you look into their board,
    otherwise you look only at p1's board
     */

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long gameId;

    @OneToOne
    private Board player1Board;

    @OneToOne
    private Board player2Board;

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public Board getPlayer1Board() {
        return player1Board;
    }

    public void setPlayer1Board(Board player1Board) {
        this.player1Board = player1Board;
    }

    public Board getPlayer2() {
        return player2Board;
    }

    public void setPlayer2(Board player2Board) {
        this.player2Board = player2Board;
    }
}