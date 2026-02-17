package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.security;

import java.util.Set;
import java.util.stream.Collectors;

import javax.security.auth.Subject;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto.ErrorResponseDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.ErrorResponseFactory;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.auth.AuthenticatedUser;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.auth.TokenSecurityContext;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.jaas.callback.TokenCallbackHandler;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.jaas.principal.RolePrincipal;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.jaas.principal.UserPrincipal;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthTokenFilter implements ContainerRequestFilter {
	private static final String TOKEN_DOMAIN = "MasterAnnonceToken";

	public AuthTokenFilter() {
		assertJaasConfigurationLoaded();
	}

	@Override
	public void filter(ContainerRequestContext requestContext) {
		if (!isProtectedEndpoint(requestContext)) {
			return;
		}

		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			abortUnauthorized(requestContext, "Missing or invalid Authorization header.");
			return;
		}

		String token = authorizationHeader.substring("Bearer ".length()).trim();
		if (token.isEmpty()) {
			abortUnauthorized(requestContext, "Missing bearer token.");
			return;
		}

		AuthenticatedUser authenticatedUser = authenticateTokenWithJaas(token);
		if (authenticatedUser == null) {
			abortUnauthorized(requestContext, "Invalid or expired token.");
			return;
		}

		SecurityContext currentContext = requestContext.getSecurityContext();
		boolean isSecure = currentContext != null && currentContext.isSecure();
		requestContext.setSecurityContext(new TokenSecurityContext(authenticatedUser, isSecure));
	}

	private boolean isProtectedEndpoint(ContainerRequestContext requestContext) {
		String method = requestContext.getMethod();
		String path = requestContext.getUriInfo().getPath(false);

		if (path == null) {
			return false;
		}
		if ("login".equals(path)) {
			return false;
		}
		if (isPublicReadEndpoint(method, path)) {
			return false;
		}

		return path.startsWith("annonces") || path.startsWith("categories");
	}

	private boolean isPublicReadEndpoint(String method, String path) {
		if (!"GET".equalsIgnoreCase(method)) {
			return false;
		}
		if ("annonces".equals(path) || path.matches("^annonces/\\d+$")) {
			return true;
		}
		return "categories".equals(path) || path.matches("^categories/\\d+$");
	}

	private AuthenticatedUser authenticateTokenWithJaas(String token) {
		try {
			LoginContext loginContext = new LoginContext(TOKEN_DOMAIN, new TokenCallbackHandler(token));
			loginContext.login();
			Subject subject = loginContext.getSubject();

			UserPrincipal userPrincipal = subject.getPrincipals(UserPrincipal.class).stream().findFirst().orElse(null);
			if (userPrincipal == null) {
				return null;
			}
			Set<String> roles = subject.getPrincipals(RolePrincipal.class).stream().map(RolePrincipal::getName)
					.collect(Collectors.toSet());
			return new AuthenticatedUser(userPrincipal.getUserId(), userPrincipal.getName(), roles);
		} catch (LoginException ex) {
			return null;
		}
	}

	private void abortUnauthorized(ContainerRequestContext requestContext, String message) {
		ErrorResponseDto error = ErrorResponseFactory.of(Response.Status.UNAUTHORIZED, message);
		Response response = Response.status(Response.Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON).entity(error)
				.build();
		requestContext.abortWith(response);
	}

	private void assertJaasConfigurationLoaded() {
		AppConfigurationEntry[] entries = Configuration.getConfiguration().getAppConfigurationEntry(TOKEN_DOMAIN);
		if (entries == null || entries.length == 0) {
			throw new IllegalStateException(
					"JAAS domain 'MasterAnnonceToken' is not configured. Set -Djava.security.auth.login.config.");
		}
	}
}
