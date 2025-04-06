package com.jbmotos.services;

import com.jbmotos.api.dto.AuthenticationDTO;
import com.jbmotos.api.dto.UserCredentialsDTO;
import org.springframework.security.core.Authentication;

public interface UserCredentialsService {

    Authentication login(AuthenticationDTO authenticationDTO);

    void registerUser(UserCredentialsDTO userCredentialsDTO);

}
