package ch.uzh.ifi.hase.soprafs21.rest.dto;

public class LobbiesGetDTO {
    private Long lobbyId;
    private String ownerName;

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
