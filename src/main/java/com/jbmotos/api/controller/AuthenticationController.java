package com.jbmotos.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jbmotos.api.dto.AuthenticationDTO;
import com.jbmotos.api.dto.UserCredentialsDTO;
import com.jbmotos.api.dto.LoginResponseDTO;
import com.jbmotos.config.security.TokenService;
import com.jbmotos.model.entity.UserCredentials;
import com.jbmotos.services.UserCredentialsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final UserCredentialsService service;
    private final TokenService tokenService;

    public AuthenticationController(UserCredentialsService service, TokenService tokenService) {
        this.service = service;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationDTO data) {
        var auth = this.service.login(data);

        var token = this.tokenService.generateToken((UserCredentials) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/save")
    public ResponseEntity<UserCredentialsDTO> cadastrar(@RequestBody @Valid UserCredentialsDTO data) {
        this.service.registerUser(data);

        return ResponseEntity.ok().build();
    }

}
