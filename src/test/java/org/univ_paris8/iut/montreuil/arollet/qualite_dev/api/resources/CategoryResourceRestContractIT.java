package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

class CategoryResourceRestContractIT extends RestJerseyTestSupport {
	@BeforeEach
	void resetData() {
		TestDataSupport.resetMainPersistenceData();
	}

	@Override
	protected void registerResources(ResourceConfig config) {
		config.register(new TestAuthenticatedUserFilter());
		config.register(CategoryResource.class);
	}

	@Test
	void shouldReturn200ForCategoryList() {
		Response response = target("categories").queryParam("page", 0).queryParam("size", 20).request().get();
		Map<String, Object> payload = asJsonMap(response.readEntity(String.class));

		assertEquals(200, response.getStatus());
		assertEquals(2, ((Number) payload.get("totalItems")).intValue());
		assertTrue(payload.containsKey("items"));
	}

	@Test
	void shouldReturn200ForCategoryDetail() {
		Response response = target("categories/1").request().get();
		Map<String, Object> payload = asJsonMap(response.readEntity(String.class));

		assertEquals(200, response.getStatus());
		assertEquals(1, ((Number) payload.get("id")).intValue());
		assertEquals("Housing", payload.get("label"));
	}

	@Test
	void shouldCreateCategoryAndReturn201() {
		Response response = target("categories").request().header("X-Test-User", "1")
				.post(jakarta.ws.rs.client.Entity.json("{\"label\":\"Vehicles\"}"));
		Map<String, Object> payload = asJsonMap(response.readEntity(String.class));

		assertEquals(201, response.getStatus());
		assertEquals(100, ((Number) payload.get("id")).intValue());
		assertEquals("Vehicles", payload.get("label"));
	}

	@Test
	void shouldUpdateCategoryAndReturn200() {
		Response response = target("categories/1").request().header("X-Test-User", "1")
				.put(jakarta.ws.rs.client.Entity.json("{\"label\":\"Rentals\"}"));
		Map<String, Object> payload = asJsonMap(response.readEntity(String.class));

		assertEquals(200, response.getStatus());
		assertEquals("Rentals", payload.get("label"));
	}

	@Test
	void shouldDeleteCategoryAndReturn204() {
		Response createResponse = target("categories").request().header("X-Test-User", "1")
				.post(jakarta.ws.rs.client.Entity.json("{\"label\":\"Temporary\"}"));
		Map<String, Object> createPayload = asJsonMap(createResponse.readEntity(String.class));
		Long createdId = ((Number) createPayload.get("id")).longValue();

		Response deleteResponse = target("categories/" + createdId).request().header("X-Test-User", "1").delete();
		assertEquals(204, deleteResponse.getStatus());
	}

	@Test
	void shouldReturn400ForValidationFailure() {
		Response response = target("categories").request().header("X-Test-User", "1")
				.post(jakarta.ws.rs.client.Entity.json("{\"label\":\"\"}"));
		Map<String, Object> errorPayload = asJsonMap(response.readEntity(String.class));

		assertStandardErrorPayload(400, "VALIDATION_ERROR", response, errorPayload);
	}

	@Test
	void shouldReturn401WhenCreateWithoutAuthentication() {
		Response response = target("categories").request()
				.post(jakarta.ws.rs.client.Entity.json("{\"label\":\"Vehicles\"}"));
		Map<String, Object> errorPayload = asJsonMap(response.readEntity(String.class));

		assertStandardErrorPayload(401, "UNAUTHORIZED", response, errorPayload);
	}

	@Test
	void shouldReturn404WhenCategoryNotFound() {
		Response response = target("categories/404").request().get();
		Map<String, Object> errorPayload = asJsonMap(response.readEntity(String.class));

		assertStandardErrorPayload(404, "NOT_FOUND", response, errorPayload);
	}

	@Test
	void shouldReturn409WhenCategoryLabelAlreadyExists() {
		Response response = target("categories").request().header("X-Test-User", "1")
				.post(jakarta.ws.rs.client.Entity.json("{\"label\":\"Housing\"}"));
		Map<String, Object> errorPayload = asJsonMap(response.readEntity(String.class));

		assertStandardErrorPayload(409, "CONFLICT", response, errorPayload);
	}

	@Test
	void shouldReturn409WhenCategoryUsedByAnnonceIsDeleted() {
		Response response = target("categories/1").request().header("X-Test-User", "1").delete();
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
