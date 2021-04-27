package ch.uzh.ifi.hase.soprafs21.rest.dto;

public class UserPostDTO {
    private String token;
    private long userId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getUserId() { return userId;}

    public void setUserId(long userId) {this.userId = userId;}
}
