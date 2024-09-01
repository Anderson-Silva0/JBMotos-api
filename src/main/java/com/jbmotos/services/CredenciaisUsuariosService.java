package com.jbmotos.services;

import com.jbmotos.api.dto.AuthenticationDTO;
import com.jbmotos.api.dto.CredenciaisUsuariosDTO;
import org.springframework.security.core.Authentication;

public interface CredenciaisUsuariosService {

    Authentication login(AuthenticationDTO authenticationDTO);

    void cadastrarUsuario(CredenciaisUsuariosDTO credenciaisUsuariosDTO);

}
