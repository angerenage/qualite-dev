package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LoginRequest", description = "Credentials payload for /api/login")
public class LoginRequestDto {
	@Schema(example = "alice")
	@NotBlank(message = "username is required.")
	@Size(max = 64, message = "username must contain at most 64 characters.")
	private String username;

	@Schema(example = "alice-pass")
	@NotBlank(message = "password is required.")
	@Size(max = 255, message = "password must contain at most 255 characters.")
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
