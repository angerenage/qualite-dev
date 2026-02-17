package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LoginResponse", description = "Authentication success payload")
public class LoginResponseDto {
	@Schema(example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
	private String token;
	@Schema(example = "3600")
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
