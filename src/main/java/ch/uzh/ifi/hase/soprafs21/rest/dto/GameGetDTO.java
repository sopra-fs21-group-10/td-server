package ch.uzh.ifi.hase.soprafs21.rest.dto;

import java.util.Map;

public class GameGetDTO {
    private Map<String, Object> player1;
    private Map<String, Object> player2;// empty in single player

    public Map<String, Object> getPlayer1() {return player1;}

    public void setPlayer1(Map<String, Object> player1) {this.player1 = player1;}

    public Map<String, Object> getPlayer2() {return player2;}

    public void setPlayer2(Map<String, Object> player2) {this.player2 = player2;}
}
