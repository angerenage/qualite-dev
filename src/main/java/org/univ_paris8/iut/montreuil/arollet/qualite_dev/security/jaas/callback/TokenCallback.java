package org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.jaas.callback;

import javax.security.auth.callback.Callback;

public class TokenCallback implements Callback {
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
