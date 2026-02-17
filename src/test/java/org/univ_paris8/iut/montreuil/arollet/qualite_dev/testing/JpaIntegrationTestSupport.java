package org.univ_paris8.iut.montreuil.arollet.qualite_dev.testing;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class JpaIntegrationTestSupport {
	protected static EntityManagerFactory emf;
	protected EntityManager em;

	@BeforeAll
	static void beforeAllIntegrationTests() {
		emf = Persistence.createEntityManagerFactory("qualite-dev-test-pu");
	}

	@AfterAll
	static void afterAllIntegrationTests() {
		if (emf != null && emf.isOpen()) {
			emf.close();
		}
	}

	@BeforeEach
	void setUpEntityManagerAndDataset() {
		em = emf.createEntityManager();
		loadSqlScript("sql/schema-h2.sql");
		loadSqlScript("sql/dataset.sql");
	}

	@AfterEach
	void closeEntityManager() {
		if (em != null && em.isOpen()) {
			em.close();
		}
	}

	private void loadSqlScript(String classpathLocation) {
		try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(classpathLocation)) {
			assertNotNull(is, "Missing SQL script on classpath: " + classpathLocation);
			String sql = readAll(is);
			List<String> statements = splitStatements(sql);

			em.getTransaction().begin();
			for (String statement : statements) {
				em.createNativeQuery(statement).executeUpdate();
			}
			em.getTransaction().commit();
		} catch (IOException ex) {
			throw new IllegalStateException("Failed to load SQL script: " + classpathLocation, ex);
		} catch (RuntimeException ex) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw ex;
		}
	}

	private String readAll(InputStream inputStream) throws IOException {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append('\n');
			}
		}
		return sb.toString();
	}

	private List<String> splitStatements(String sql) {
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
