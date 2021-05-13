package ch.uzh.ifi.hase.soprafs21.rest.dto;

import java.util.List;

public class GameWaveDTO {
    private List<String> player1Minions;

    private List<String> player2Minions;

    public List<String> getPlayer1Minions() {
        return player1Minions;
    }

    public void setPlayer1Minions(List<String> player1Minions) {
        this.player1Minions = player1Minions;
    }

    public List<String> getPlayer2Minions() {
        return player2Minions;
    }

    public void setPlayer2Minions(List<String> player2Minions) {
        this.player2Minions = player2Minions;
    }
}
