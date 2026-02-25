package org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto;

public class LoginResponseDto {

    private String token;
    private long expiresIn;

    public LoginResponseDto() {
    }

    public LoginResponseDto(String token, long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
