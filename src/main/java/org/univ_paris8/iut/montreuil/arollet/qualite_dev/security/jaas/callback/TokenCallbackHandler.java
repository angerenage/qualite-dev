package org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.jaas.callback;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class TokenCallbackHandler implements CallbackHandler {
	private final String token;

	public TokenCallbackHandler(String token) {
		this.token = token;
	}

	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		for (Callback callback : callbacks) {
			if (callback instanceof TokenCallback) {
				((TokenCallback) callback).setToken(token);
				continue;
			}
			if (callback instanceof NameCallback) {
				((NameCallback) callback).setName(token);
				continue;
			}
			throw new UnsupportedCallbackException(callback);
		}
	}
}
