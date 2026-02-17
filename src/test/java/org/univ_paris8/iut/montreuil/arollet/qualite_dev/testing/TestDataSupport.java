package org.univ_paris8.iut.montreuil.arollet.qualite_dev.testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.SessionFactory;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.auth.TokenService;

public final class TestDataSupport {
	private TestDataSupport() {
	}

	public static void resetMainPersistenceData() {
		EntityManager em = SessionFactory.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			executeScript(em, "sql/schema-h2.sql");
			executeScript(em, "sql/dataset.sql");
			tx.commit();
		} catch (RuntimeException ex) {
			if (tx.isActive()) {
				tx.rollback();
			}
			throw ex;
		} finally {
			em.close();
		}
	}

	public static void clearAllTokens() {
		TokenService.getInstance().clearTokensForTesting();
	}

	public static void forceExpireToken(String token, Long userId) {
		TokenService.getInstance().forceExpireTokenForTesting(token, userId);
	}

	private static void executeScript(EntityManager em, String classpathLocation) {
		String sql = readClasspathResource(classpathLocation);
		for (String statement : splitStatements(sql)) {
			em.createNativeQuery(statement).executeUpdate();
		}
	}

	private static String readClasspathResource(String classpathLocation) {
		try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(classpathLocation)) {
			if (is == null) {
				throw new IllegalStateException("Missing SQL script: " + classpathLocation);
			}
			StringBuilder sb = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line).append('\n');
				}
			}
			return sb.toString();
		} catch (IOException ex) {
			throw new IllegalStateException("Failed to read SQL script: " + classpathLocation, ex);
		}
	}

	private static List<String> splitStatements(String sql) {
		List<String> statements = new ArrayList<>();
		StringBuilder current = new StringBuilder();
		for (String rawLine : sql.split("\\R")) {
			String line = rawLine.trim();
			if (line.isEmpty() || line.startsWith("--")) {
				continue;
			}
			current.append(rawLine).append('\n');
			if (line.endsWith(";")) {
				String statement = current.toString().trim();
				statement = statement.substring(0, statement.length() - 1).trim();
				if (!statement.isEmpty()) {
					statements.add(statement);
				}
				current.setLength(0);
			}
		}
		String trailing = current.toString().trim();
		if (!trailing.isEmpty()) {
			statements.add(trailing);
		}
		return statements;
	}
}
