package org.univ_paris8.iut.montreuil.arollet.qualite_dev.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.config.JwtProperties;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.entity.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final JwtProperties properties;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
    }

    public TokenPayload issueToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(properties.getExpirationSeconds());
        String token = Jwts.builder()
            .subject(user.getUsername())
            .claim("uid", user.getId())
            .claim("roles", splitRoles(user.getRoles()))
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiresAt))
            .signWith(secretKey())
            .compact();
        return new TokenPayload(token, properties.getExpirationSeconds());
    }

    public Jws<Claims> parse(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey()).build().parseSignedClaims(token);
        } catch (JwtException | IllegalArgumentException ex) {
            return null;
        }
    }

    private SecretKey secretKey() {
        byte[] raw = properties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(raw);
    }

    private List<String> splitRoles(String roles) {
        if (roles == null || roles.isBlank()) {
            throw new IllegalStateException("User roles cannot be empty.");
        }
        List<String> parsedRoles = Arrays.stream(roles.split(","))
            .map(String::trim)
            .filter(value -> !value.isEmpty())
            .toList();
        if (parsedRoles.isEmpty() || parsedRoles.stream().anyMatch(value -> !value.startsWith("ROLE_"))) {
            throw new IllegalStateException("User roles must use ROLE_* values.");
        }
        return parsedRoles;
    }

    public record TokenPayload(String token, long expiresIn) {
    }
}
