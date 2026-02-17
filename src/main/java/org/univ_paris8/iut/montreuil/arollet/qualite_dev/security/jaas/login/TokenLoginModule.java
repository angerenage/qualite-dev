package org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.jaas.login;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.jaas.callback.TokenCallback;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.jaas.principal.RolePrincipal;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.jaas.principal.UserPrincipal;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.jaas.support.JaasUserRecord;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.jaas.support.JaasUserStore;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.auth.TokenService;

public class TokenLoginModule implements LoginModule {
	private Subject subject;
	private CallbackHandler callbackHandler;
	private JaasUserRecord authenticatedUser;
	private UserPrincipal userPrincipal;
	private final Set<RolePrincipal> rolePrincipals = new HashSet<>();
	private boolean loginSucceeded;
	private boolean commitSucceeded;

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
			Map<String, ?> options) {
		this.subject = subject;
		this.callbackHandler = callbackHandler;
	}

	@Override
	public boolean login() throws LoginException {
		if (callbackHandler == null) {
			throw new LoginException("No CallbackHandler provided.");
		}

		TokenCallback tokenCallback = new TokenCallback();
		NameCallback fallbackNameCallback = new NameCallback("token");
		try {
			callbackHandler.handle(new Callback[] { tokenCallback, fallbackNameCallback });
		} catch (IOException | UnsupportedCallbackException ex) {
			throw new LoginException("Unable to read token callbacks.");
		}

		String token = tokenCallback.getToken();
		if (token == null || token.trim().isEmpty()) {
			token = fallbackNameCallback.getName();
		}
		if (token == null || token.trim().isEmpty()) {
			throw new FailedLoginException("Invalid token.");
		}

		TokenService.TokenValidation validation = TokenService.getInstance().validateToken(token);
		if (validation == null) {
			throw new FailedLoginException("Invalid token.");
		}

		JaasUserRecord userRecord = JaasUserStore.findById(validation.getUserId());
		if (userRecord == null) {
			TokenService.getInstance().revokeToken(token);
			throw new FailedLoginException("Invalid token.");
		}

		this.authenticatedUser = userRecord;
		this.loginSucceeded = true;
		return true;
	}

	@Override
	public boolean commit() throws LoginException {
		if (!loginSucceeded) {
			return false;
		}

		userPrincipal = new UserPrincipal(authenticatedUser.getUsername(), authenticatedUser.getUserId());
		subject.getPrincipals().add(userPrincipal);
		for (String role : authenticatedUser.getRoles()) {
			RolePrincipal rolePrincipal = new RolePrincipal(role);
			rolePrincipals.add(rolePrincipal);
			subject.getPrincipals().add(rolePrincipal);
		}

		commitSucceeded = true;
		return true;
	}

	@Override
	public boolean abort() throws LoginException {
		if (!loginSucceeded) {
			return false;
		}
		if (!commitSucceeded) {
			loginSucceeded = false;
			authenticatedUser = null;
		} else {
			logout();
		}
		return true;
	}

	@Override
	public boolean logout() throws LoginException {
		if (subject != null) {
			if (userPrincipal != null) {
				subject.getPrincipals().remove(userPrincipal);
			}
			subject.getPrincipals().removeAll(rolePrincipals);
		}

		authenticatedUser = null;
		userPrincipal = null;
		rolePrincipals.clear();
		loginSucceeded = false;
		commitSucceeded = false;
		return true;
	}
}
