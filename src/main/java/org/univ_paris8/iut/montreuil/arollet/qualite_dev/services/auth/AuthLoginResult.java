package org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.auth;

public class AuthLoginResult {
	private final String token;
	private final long expiresIn;

	public AuthLoginResult(String token, long expiresIn) {
		this.token = token;
		this.expiresIn = expiresIn;
	}

	public String getToken() {
		return token;
	}

	public long getExpiresIn() {
		return expiresIn;
	}
}
