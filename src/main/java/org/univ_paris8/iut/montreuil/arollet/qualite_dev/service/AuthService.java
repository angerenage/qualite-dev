package org.univ_paris8.iut.montreuil.arollet.qualite_dev.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.entity.User;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.repository.UserRepository;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.JwtService;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto.LoginResponseDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.error.ApiException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordHasher passwordHasher, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResponseDto login(String username, String password) {
        User user = userRepository.findByUsernameIgnoreCase(username)
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials."));

        if (!passwordHasher.matches(password, user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials.");
        }

        JwtService.TokenPayload token = jwtService.issueToken(user);
        return new LoginResponseDto(token.token(), token.expiresIn());
    }
}
