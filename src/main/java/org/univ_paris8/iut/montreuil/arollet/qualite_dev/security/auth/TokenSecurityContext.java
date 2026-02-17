package org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.auth;

import java.security.Principal;

import jakarta.ws.rs.core.SecurityContext;

public class TokenSecurityContext implements SecurityContext {
	private final AuthenticatedUser authenticatedUser;
	private final boolean secureRequest;

	public TokenSecurityContext(AuthenticatedUser authenticatedUser, boolean secureRequest) {
		this.authenticatedUser = authenticatedUser;
		this.secureRequest = secureRequest;
	}

	@Override
	public Principal getUserPrincipal() {
		return authenticatedUser;
	}

	@Override
	public boolean isUserInRole(String role) {
		if (role == null || authenticatedUser == null) {
			return false;
		}
		return authenticatedUser.getRoles().stream().anyMatch(r -> r.equalsIgnoreCase(role));
	}

	@Override
	public boolean isSecure() {
		return secureRequest;
	}

	@Override
	public String getAuthenticationScheme() {
		return "Bearer";
	}
}
