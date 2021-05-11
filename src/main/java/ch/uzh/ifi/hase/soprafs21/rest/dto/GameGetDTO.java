package ch.uzh.ifi.hase.soprafs21.rest.dto;

import java.util.Map;

public class GameGetDTO {
    private Map<String, Object> player1;
    private Map<String, Object> player2;// empty in single player

    private long gameId;
    private int round;

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public Map<String, Object> getPlayer1() {return player1;}

    public void setPlayer1(Map<String, Object> player1) {this.player1 = player1;}

    public Map<String, Object> getPlayer2() {return player2;}

    public void setPlayer2(Map<String, Object> player2) {this.player2 = player2;}
}
