package org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.jaas.support;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.SessionFactory;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.User;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.repositories.UserRepository;

public final class JaasUserStore {
	private static final UserRepository USER_REPOSITORY = new UserRepository();

	private JaasUserStore() {
	}

	public static JaasUserRecord findByUsername(String username) {
		EntityManager em = SessionFactory.getEntityManager();
		try {
			User user = USER_REPOSITORY.findByUsername(em, username);
			return toRecord(user);
		} finally {
			em.close();
		}
	}

	public static JaasUserRecord findById(Long userId) {
		EntityManager em = SessionFactory.getEntityManager();
		try {
			User user = USER_REPOSITORY.findById(em, userId);
			return toRecord(user);
		} finally {
			em.close();
		}
	}

	private static JaasUserRecord toRecord(User user) {
		if (user == null) {
			return null;
		}
		return new JaasUserRecord(user.getId(), user.getUsername(), user.getPasswordHash(), parseRoles(user.getRoles()));
	}

	private static Set<String> parseRoles(String rawRoles) {
		if (rawRoles == null || rawRoles.trim().isEmpty()) {
			return Set.of("USER");
		}
		return Arrays.stream(rawRoles.split(",")).map(String::trim).filter(role -> !role.isEmpty())
				.map(role -> role.toUpperCase(Locale.ROOT)).collect(Collectors.toSet());
	}
}
