package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.Configuration;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.ApiException;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.ForbiddenException;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.security.AuthTokenFilter;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.auth.AuthenticatedUser;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.testing.TestDataSupport;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

class SecurityRestFlowIT extends RestJerseyTestSupport {
	static {
		String jaasConfigPath = Paths.get("jaas.conf").toAbsolutePath().toString();
		System.setProperty("java.security.auth.login.config", jaasConfigPath);
		Configuration.getConfiguration().refresh();
	}

	@BeforeEach
	void resetDataAndTokenStore() {
		TestDataSupport.resetMainPersistenceData();
		TestDataSupport.clearAllTokens();
	}

	@Override
	protected void registerResources(ResourceConfig config) {
		config.register(AuthResource.class);
		config.register(AuthTokenFilter.class);
		config.register(SecurityProbeResource.class);
	}

	@Test
	void shouldLoginAndReturnToken() {
		Response response = target("login").request()
				.post(Entity.json("{\"username\":\"alice\",\"password\":\"alice-pass\"}"));
		Map<String, Object> payload = asJsonMap(response.readEntity(String.class));

		assertEquals(200, response.getStatus());
		assertNotNull(payload.get("token"));
		assertEquals(3600, ((Number) payload.get("expiresIn")).intValue());
	}

	@Test
	void shouldReturn401ForInvalidCredentials() {
		Response response = target("login").request()
				.post(Entity.json("{\"username\":\"alice\",\"password\":\"wrong\"}"));
		Map<String, Object> errorPayload = asJsonMap(response.readEntity(String.class));

		assertStandardErrorPayload(401, "UNAUTHORIZED", response, errorPayload);
	}

	@Test
	void shouldReturn400ForMalformedLoginRequest() {
		Response response = target("login").request().post(Entity.json("{\"username\":\"alice\""));
		Map<String, Object> errorPayload = asJsonMap(response.readEntity(String.class));

		assertStandardErrorPayload(400, "VALIDATION_ERROR", response, errorPayload);
	}

	@Test
	void shouldReturn401ForProtectedEndpointWithoutToken() {
		Response response = target("annonces/probe").request().post(Entity.json("{}"));
		Map<String, Object> errorPayload = asJsonMap(response.readEntity(String.class));

		assertStandardErrorPayload(401, "UNAUTHORIZED", response, errorPayload);
	}

	@Test
	void shouldAllowProtectedEndpointWithValidToken() {
		String token = loginAndGetToken("alice", "alice-pass");

		Response response = target("annonces/probe").request().header("Authorization", "Bearer " + token)
				.post(Entity.json("{}"));
		Map<String, Object> payload = asJsonMap(response.readEntity(String.class));

		assertEquals(200, response.getStatus());
		assertEquals(1, ((Number) payload.get("userId")).intValue());
	}

	@Test
	void shouldReturn401ForExpiredToken() {
		String token = loginAndGetToken("alice", "alice-pass");
		TestDataSupport.forceExpireToken(token, 1L);

		Response response = target("annonces/probe").request().header("Authorization", "Bearer " + token)
				.post(Entity.json("{}"));
		Map<String, Object> errorPayload = asJsonMap(response.readEntity(String.class));

		assertStandardErrorPayload(401, "UNAUTHORIZED", response, errorPayload);
	}

	@Test
	void shouldReturn403WhenAuthenticatedUserIsNotAuthor() {
		String token = loginAndGetToken("bob", "bob-pass");

		Response response = target("annonces/probe/owner/1").request().header("Authorization", "Bearer " + token)
				.post(Entity.json("{}"));
		Map<String, Object> errorPayload = asJsonMap(response.readEntity(String.class));

		assertStandardErrorPayload(403, "FORBIDDEN", response, errorPayload);
	}

	private String loginAndGetToken(String username, String password) {
		Response response = target("login").request()
				.post(Entity.json("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"));
		Map<String, Object> payload = asJsonMap(response.readEntity(String.class));
		return payload.get("token").toString();
	}

	@Path("/annonces/probe")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public static class SecurityProbeResource {
		@POST
		public Response probe(@Context SecurityContext securityContext) {
			AuthenticatedUser user = requireAuthenticatedUser(securityContext);
			Map<String, Object> payload = new HashMap<>();
			payload.put("ok", Boolean.TRUE);
			payload.put("userId", user.getUserId());
			payload.put("username", user.getName());
			return Response.ok(payload).build();
		}

		@POST
		@Path("/owner/{authorId}")
		public Response ownerProtectedAction(@PathParam("authorId") Long authorId, @Context SecurityContext securityContext) {
			AuthenticatedUser user = requireAuthenticatedUser(securityContext);
			if (!authorId.equals(user.getUserId())) {
				throw new ForbiddenException("Only the author can modify this annonce.");
			}
			Map<String, Object> payload = new HashMap<>();
			payload.put("ok", Boolean.TRUE);
			return Response.ok(payload).build();
		}

		private AuthenticatedUser requireAuthenticatedUser(SecurityContext securityContext) {
			if (securityContext == null || !(securityContext.getUserPrincipal() instanceof AuthenticatedUser)) {
				throw new ApiException(Response.Status.UNAUTHORIZED, "Authentication is required.");
			}
			return (AuthenticatedUser) securityContext.getUserPrincipal();
		}
	}
}
