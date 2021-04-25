package ch.uzh.ifi.hase.soprafs21.rest.dto;

public class LobbyPutAndPatchDTO {
    private String token;
    private Long lobbyId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }
}
