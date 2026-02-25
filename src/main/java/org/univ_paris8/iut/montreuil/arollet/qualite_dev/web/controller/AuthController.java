package org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.service.AuthService;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto.LoginRequestDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto.LoginResponseDto;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponseDto login(@Valid @RequestBody LoginRequestDto request) {
        return authService.login(request.getUsername(), request.getPassword());
    }
}
