package org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.jaas.support;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JaasUserRecord {
	private final Long userId;
	private final String username;
	private final String passwordHash;
	private final Set<String> roles;

	public JaasUserRecord(Long userId, String username, String passwordHash, Set<String> roles) {
		this.userId = userId;
		this.username = username;
		this.passwordHash = passwordHash;
		this.roles = roles == null ? Collections.emptySet() : Collections.unmodifiableSet(new HashSet<>(roles));
	}

	public Long getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public Set<String> getRoles() {
		return roles;
	}
}
