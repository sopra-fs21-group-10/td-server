package ch.uzh.ifi.hase.soprafs21.rest.dto;

public class LobbiesGetDTO {
    private Long lobbyId;
    private String lobbyOwner;

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }

    public String getLobbyOwner() {
        return lobbyOwner;
    }

    public void setLobbyOwner(String lobbyOwner) {
        this.lobbyOwner = lobbyOwner;
    }
}
