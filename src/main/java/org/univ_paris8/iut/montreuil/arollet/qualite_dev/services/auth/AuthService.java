package org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.auth;

import javax.security.auth.Subject;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.ApiException;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.jaas.callback.UsernamePasswordCallbackHandler;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.jaas.principal.UserPrincipal;

import jakarta.ws.rs.core.Response;

public class AuthService {
	private static final String LOGIN_DOMAIN = "MasterAnnonceLogin";
	private static final AuthService INSTANCE = new AuthService(TokenService.getInstance());
	private final TokenService tokenService;

	public static AuthService getInstance() {
		return INSTANCE;
	}

	AuthService(TokenService tokenService) {
		this.tokenService = tokenService;
		assertJaasConfigurationLoaded();
	}

	public AuthLoginResult authenticate(String username, String plainPassword) {
		if (username == null || username.trim().isEmpty() || plainPassword == null || plainPassword.isEmpty()) {
			throw new ApiException(Response.Status.UNAUTHORIZED, "Invalid credentials.");
		}

		try {
			LoginContext loginContext = new LoginContext(LOGIN_DOMAIN,
					new UsernamePasswordCallbackHandler(username, plainPassword));
			loginContext.login();

			Subject subject = loginContext.getSubject();
			UserPrincipal userPrincipal = subject.getPrincipals(UserPrincipal.class).stream().findFirst()
					.orElseThrow(() -> new ApiException(Response.Status.UNAUTHORIZED, "Invalid credentials."));

			TokenService.IssuedToken issuedToken = tokenService.issueToken(userPrincipal.getUserId());
			return new AuthLoginResult(issuedToken.getToken(), issuedToken.getExpiresIn());
		} catch (LoginException ex) {
			throw new ApiException(Response.Status.UNAUTHORIZED, "Invalid credentials.");
		}
	}

	private void assertJaasConfigurationLoaded() {
		AppConfigurationEntry[] entries = Configuration.getConfiguration().getAppConfigurationEntry(LOGIN_DOMAIN);
		if (entries == null || entries.length == 0) {
			throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR,
					"JAAS domain 'MasterAnnonceLogin' is not configured. Set -Djava.security.auth.login.config.");
		}
	}
}
