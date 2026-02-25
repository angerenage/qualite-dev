package org.univ_paris8.iut.montreuil.arollet.qualite_dev.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.entity.User;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.repository.UserRepository;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.JwtService;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.error.ApiException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginRejectsInvalidPassword() {
        User user = new User();
        user.setUsername("alice");
        user.setPasswordHash("hash");

        when(userRepository.findByUsernameIgnoreCase("alice")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("wrong", "hash")).thenReturn(false);

        assertThatThrownBy(() -> authService.login("alice", "wrong"))
            .isInstanceOf(ApiException.class)
            .extracting(ex -> ((ApiException) ex).getStatus())
            .isEqualTo(HttpStatus.UNAUTHORIZED);

        verify(jwtService, never()).issueToken(any());
    }
}
