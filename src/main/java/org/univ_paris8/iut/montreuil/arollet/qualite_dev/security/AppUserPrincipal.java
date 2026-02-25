package org.univ_paris8.iut.montreuil.arollet.qualite_dev.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AppUserPrincipal implements UserDetails {

    private final Long userId;
    private final String username;
    private final String passwordHash;
    private final List<SimpleGrantedAuthority> authorities;

    public AppUserPrincipal(Long userId, String username, String passwordHash, List<SimpleGrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.authorities = authorities;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
