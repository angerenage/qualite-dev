package org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.jaas.principal;

import java.security.Principal;
import java.util.Objects;

public class UserPrincipal implements Principal {
	private final String username;
	private final Long userId;

	public UserPrincipal(String username, Long userId) {
		this.username = username;
		this.userId = userId;
	}

	@Override
	public String getName() {
		return username;
	}

	public Long getUserId() {
		return userId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof UserPrincipal)) {
			return false;
		}
		UserPrincipal other = (UserPrincipal) obj;
		return Objects.equals(username, other.username) && Objects.equals(userId, other.userId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(username, userId);
	}
}
