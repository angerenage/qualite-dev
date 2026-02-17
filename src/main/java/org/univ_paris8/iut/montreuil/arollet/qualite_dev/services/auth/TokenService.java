package org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.auth;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TokenService {
	private static final long DEFAULT_EXPIRES_IN_SECONDS = 3600L;
	private static final TokenService INSTANCE = new TokenService(Duration.ofSeconds(DEFAULT_EXPIRES_IN_SECONDS));
	private final Map<String, StoredToken> tokens = new ConcurrentHashMap<>();
	private final Duration tokenLifetime;

	public static TokenService getInstance() {
		return INSTANCE;
	}

	TokenService(Duration tokenLifetime) {
		this.tokenLifetime = tokenLifetime;
	}

	public IssuedToken issueToken(Long userId) {
		evictExpiredTokens();
		if (userId == null || userId <= 0) {
			throw new IllegalArgumentException("userId must be positive.");
		}
		String token = UUID.randomUUID().toString();
		Instant expiresAt = Instant.now().plus(tokenLifetime);
		tokens.put(token, new StoredToken(userId, expiresAt));
		return new IssuedToken(token, expiresAt, tokenLifetime.getSeconds());
	}

	public TokenValidation validateToken(String token) {
		if (token == null || token.trim().isEmpty()) {
			return null;
		}

		evictExpiredTokens();
		StoredToken stored = tokens.get(token.trim());
		if (stored == null) {
			return null;
		}
		if (stored.expiresAt.isBefore(Instant.now())) {
			tokens.remove(token.trim());
			return null;
		}
		return new TokenValidation(token.trim(), stored.userId, stored.expiresAt);
	}

	public void revokeToken(String token) {
		if (token != null) {
			tokens.remove(token.trim());
		}
	}

	public void clearTokensForTesting() {
		tokens.clear();
	}

	public void forceExpireTokenForTesting(String token, Long userId) {
		if (token == null || token.trim().isEmpty() || userId == null || userId <= 0) {
			return;
		}
		tokens.put(token.trim(), new StoredToken(userId, Instant.now().minusSeconds(60)));
	}

	private void evictExpiredTokens() {
		Instant now = Instant.now();
		tokens.entrySet().removeIf(entry -> entry.getValue().expiresAt.isBefore(now));
	}

	private static class StoredToken {
		private final Long userId;
		private final Instant expiresAt;

		private StoredToken(Long userId, Instant expiresAt) {
			this.userId = userId;
			this.expiresAt = expiresAt;
		}
	}

	public static class TokenValidation {
		private final String token;
		private final Long userId;
		private final Instant expiresAt;

		public TokenValidation(String token, Long userId, Instant expiresAt) {
			this.token = token;
			this.userId = userId;
			this.expiresAt = expiresAt;
		}

		public String getToken() {
			return token;
		}

		public Long getUserId() {
			return userId;
		}

		public Instant getExpiresAt() {
			return expiresAt;
		}
	}

	public static class IssuedToken {
		private final String token;
		private final Instant expiresAt;
		private final long expiresIn;

		public IssuedToken(String token, Instant expiresAt, long expiresIn) {
			this.token = token;
			this.expiresAt = expiresAt;
			this.expiresIn = expiresIn;
		}

		public String getToken() {
			return token;
		}

		public Instant getExpiresAt() {
			return expiresAt;
		}

		public long getExpiresIn() {
			return expiresIn;
		}
	}
}
