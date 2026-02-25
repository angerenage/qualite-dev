package org.univ_paris8.iut.montreuil.arollet.qualite_dev.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = authorization.substring(7);
            Jws<Claims> claims = jwtService.parse(token);
            if (claims != null) {
                Long userId = claims.getPayload().get("uid", Long.class);
                String username = claims.getPayload().getSubject();
                Collection<SimpleGrantedAuthority> authorities = extractAuthorities(claims.getPayload().get("roles"));
                if (userId != null && username != null && !username.isBlank() && !authorities.isEmpty()) {
                    AppUserPrincipal principal = new AppUserPrincipal(userId, username, "", List.copyOf(authorities));
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, token, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private Collection<SimpleGrantedAuthority> extractAuthorities(Object rawRoles) {
        if (!(rawRoles instanceof List<?> roles)) {
            return List.of();
        }
        return roles.stream()
            .map(Object::toString)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .filter(s -> s.startsWith("ROLE_"))
            .map(SimpleGrantedAuthority::new)
            .distinct()
            .toList();
    }
}
