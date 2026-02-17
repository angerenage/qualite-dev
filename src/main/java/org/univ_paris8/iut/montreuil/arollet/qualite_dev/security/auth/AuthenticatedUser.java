package org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.auth;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AuthenticatedUser implements Principal {
	private final Long userId;
	private final String username;
	private final Set<String> roles;

	public AuthenticatedUser(Long userId, String username, Set<String> roles) {
		this.userId = userId;
		this.username = username;
		this.roles = roles == null ? Collections.emptySet() : Collections.unmodifiableSet(new HashSet<>(roles));
	}

	public Long getUserId() {
		return userId;
	}

	@Override
	public String getName() {
		return username;
	}

	public Set<String> getRoles() {
		return roles;
	}
}
