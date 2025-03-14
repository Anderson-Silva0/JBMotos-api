package com.jbmotos.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jbmotos.api.dto.AuthenticationDTO;
import com.jbmotos.api.dto.CredenciaisUsuariosDTO;
import com.jbmotos.api.dto.LoginResponseDTO;
import com.jbmotos.config.security.TokenService;
import com.jbmotos.model.entity.CredenciaisUsuarios;
import com.jbmotos.services.CredenciaisUsuariosService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private CredenciaisUsuariosService service;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationDTO data) {
        var auth = this.service.login(data);

        var token = tokenService.generateToken((CredenciaisUsuarios) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<CredenciaisUsuariosDTO> cadastrar(@RequestBody @Valid CredenciaisUsuariosDTO data) {
        this.service.cadastrarUsuario(data);

        return ResponseEntity.ok().build();
    }

}
