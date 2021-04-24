package ch.uzh.ifi.hase.soprafs21.rest.dto;

import java.util.HashMap;

public class GameGetDTO {
    private HashMap<String, Object> player1;
    private HashMap<String, Object> player2;// empty in single player

    public HashMap<String, Object> getPlayer1() {return player1;}

    public void setPlayer1(HashMap<String, Object> player1) {this.player1 = player1;}

    public HashMap<String, Object> getPlayer2() {return player2;}

    public void setPlayer2(HashMap<String, Object> player2) {this.player2 = player2;}
}
