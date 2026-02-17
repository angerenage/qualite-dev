package org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.jaas.principal;

import java.security.Principal;
import java.util.Locale;
import java.util.Objects;

public class RolePrincipal implements Principal {
	private final String role;

	public RolePrincipal(String role) {
		this.role = role == null ? "" : role.toUpperCase(Locale.ROOT);
	}

	@Override
	public String getName() {
		return role;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof RolePrincipal)) {
			return false;
		}
		RolePrincipal other = (RolePrincipal) obj;
		return Objects.equals(role, other.role);
	}

	@Override
	public int hashCode() {
		return Objects.hash(role);
	}
}
