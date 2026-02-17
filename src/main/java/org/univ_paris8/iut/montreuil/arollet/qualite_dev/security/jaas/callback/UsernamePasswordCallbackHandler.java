package org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.jaas.callback;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class UsernamePasswordCallbackHandler implements CallbackHandler {
	private final String username;
	private final char[] password;

	public UsernamePasswordCallbackHandler(String username, String password) {
		this.username = username;
		this.password = password == null ? new char[0] : password.toCharArray();
	}

	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		for (Callback callback : callbacks) {
			if (callback instanceof NameCallback) {
				((NameCallback) callback).setName(username);
				continue;
			}
			if (callback instanceof PasswordCallback) {
				((PasswordCallback) callback).setPassword(password);
				continue;
			}
			throw new UnsupportedCallbackException(callback);
		}
	}
}
