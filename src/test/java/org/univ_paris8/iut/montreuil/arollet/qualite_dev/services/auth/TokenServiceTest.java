package org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TokenServiceTest {
	private final TokenService tokenService = TokenService.getInstance();

	@BeforeEach
	void cleanStore() {
		tokenService.clearTokensForTesting();
	}

	@Test
	void shouldIssueOpaqueTokenWithDefaultLifetime() {
		TokenService.IssuedToken issuedToken = tokenService.issueToken(1L);

		assertNotNull(issuedToken.getToken());
		assertEquals(3600L, issuedToken.getExpiresIn());
		assertNotNull(tokenService.validateToken(issuedToken.getToken()));
	}

	@Test
	void shouldRejectExpiredToken() {
		TokenService.IssuedToken issuedToken = tokenService.issueToken(1L);
		tokenService.forceExpireTokenForTesting(issuedToken.getToken(), 1L);

		assertNull(tokenService.validateToken(issuedToken.getToken()));
	}
}
