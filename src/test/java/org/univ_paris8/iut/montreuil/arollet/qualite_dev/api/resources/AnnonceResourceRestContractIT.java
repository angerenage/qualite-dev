package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.Set;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.auth.AuthenticatedUser;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.auth.TokenSecurityContext;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.testing.TestDataSupport;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

class AnnonceResourceRestContractIT extends RestJerseyTestSupport {
	@BeforeEach
	void resetData() {
		TestDataSupport.resetMainPersistenceData();
	}

	@Override
	protected void registerResources(ResourceConfig config) {
		config.register(new TestAuthenticatedUserFilter());
		config.register(AnnonceResource.class);
	}

	@Test
	void shouldReturn200ForAnnonceDetail() {
		Response response = target("annonces/1").request().get();
		Map<String, Object> payload = asJsonMap(response.readEntity(String.class));

		assertEquals(200, response.getStatus());
		assertEquals(1, ((Number) payload.get("id")).intValue());
		assertEquals("Annonce 1", payload.get("title"));
	}

	@Test
	void shouldCreateAnnonceAndReturn201() {
		String requestJson = "{"
				+ "\"title\":\"New title\","
				+ "\"description\":\"New description\","
				+ "\"adress\":\"Montreuil\","
				+ "\"mail\":\"new@example.com\","
				+ "\"status\":\"DRAFT\","
				+ "\"categoryId\":1"
				+ "}";

		Response response = target("annonces").request().header("X-Test-User", "1")
				.post(jakarta.ws.rs.client.Entity.json(requestJson));
		Map<String, Object> payload = asJsonMap(response.readEntity(String.class));

		assertEquals(201, response.getStatus());
		assertEquals(100, ((Number) payload.get("id")).intValue());
		assertEquals(1, ((Number) payload.get("authorId")).intValue());
		assertEquals("DRAFT", payload.get("status"));
	}

	@Test
	void shouldUpdateAnnonceAndReturn200() {
		String requestJson = "{"
				+ "\"title\":\"Updated\","
				+ "\"description\":\"Updated desc\","
				+ "\"adress\":\"Paris\","
				+ "\"mail\":\"updated@example.com\","
				+ "\"status\":\"DRAFT\","
				+ "\"categoryId\":1,"
				+ "\"version\":0"
				+ "}";

		Response response = target("annonces/1").request().header("X-Test-User", "1")
				.put(jakarta.ws.rs.client.Entity.json(requestJson));
		Map<String, Object> payload = asJsonMap(response.readEntity(String.class));

		assertEquals(200, response.getStatus());
		assertEquals("Updated", payload.get("title"));
	}

	@Test
	void shouldDeleteAnnonceAndReturn204() {
		Response response = target("annonces/3").request().header("X-Test-User", "2").delete();

		assertEquals(204, response.getStatus());
	}

	@Test
	void shouldReturn400ForValidationFailure() {
		String invalidJson = "{"
				+ "\"description\":\"Missing required title\","
				+ "\"adress\":\"Paris\","
				+ "\"mail\":\"invalid-email\","
				+ "\"categoryId\":1"
				+ "}";

		Response response = target("annonces").request().header("X-Test-User", "1")
				.post(jakarta.ws.rs.client.Entity.json(invalidJson));
		Map<String, Object> errorPayload = asJsonMap(response.readEntity(String.class));

		assertStandardErrorPayload(400, "VALIDATION_ERROR", response, errorPayload);
	}

	@Test
	void shouldReturn404WhenAnnonceNotFound() {
		Response response = target("annonces/404").request().get();
		Map<String, Object> errorPayload = asJsonMap(response.readEntity(String.class));

		assertStandardErrorPayload(404, "NOT_FOUND", response, errorPayload);
	}

	@Test
	void shouldReturn409WhenUpdateConflicts() {
		String requestJson = "{"
				+ "\"title\":\"Conflict\","
				+ "\"description\":\"Conflict\","
				+ "\"adress\":\"Paris\","
				+ "\"mail\":\"conflict@example.com\","
				+ "\"status\":\"DRAFT\","
				+ "\"categoryId\":1,"
				+ "\"version\":0"
				+ "}";

		Response response = target("annonces/2").request().header("X-Test-User", "1")
				.put(jakarta.ws.rs.client.Entity.json(requestJson));
		Map<String, Object> errorPayload = asJsonMap(response.readEntity(String.class));

		assertStandardErrorPayload(409, "CONFLICT", response, errorPayload);
	}

	@Priority(Priorities.AUTHENTICATION)
	private static class TestAuthenticatedUserFilter implements ContainerRequestFilter {
		@Override
		public void filter(ContainerRequestContext requestContext) {
			String userIdHeader = requestContext.getHeaderString("X-Test-User");
			if (userIdHeader == null || userIdHeader.isEmpty()) {
				return;
			}
			Long userId = Long.parseLong(userIdHeader);
			AuthenticatedUser user = new AuthenticatedUser(userId, "user-" + userId, Set.of("USER"));
			SecurityContext currentContext = requestContext.getSecurityContext();
			boolean secure = currentContext != null && currentContext.isSecure();
			requestContext.setSecurityContext(new TokenSecurityContext(user, secure));
		}
	}
}
