package ch.uzh.ifi.hase.soprafs21.rest.dto;

public class GameMoveDTO {
    private int[] coordinates;// x,y    len of 2,  0-14, 0-9

    public String getPlayable() {
        return playable;
    }

    public void setPlayable(String playable) {
        this.playable = playable;
    }

    private String playable;//eg. fire tower
    // it will be checked if the value is a valid one

    public int[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(int[] coordinates) {
        this.coordinates = coordinates;
    }
}
