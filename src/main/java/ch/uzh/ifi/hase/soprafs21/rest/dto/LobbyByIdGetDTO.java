package ch.uzh.ifi.hase.soprafs21.rest.dto;

public class LobbyByIdGetDTO {
    private String lobbyOwner;
    private String player2;
    private String player2Status;

    public String getLobbyOwner() {
        return lobbyOwner;
    }

    public void setLobbyOwner(String lobbyOwner) {
        this.lobbyOwner = lobbyOwner;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public String getPlayer2Status() {
        return player2Status;
    }

    public void setPlayer2Status(String player2Status) {
        this.player2Status = player2Status;
    }
}
