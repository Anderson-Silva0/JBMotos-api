package com.jbmotos.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.jbmotos.model.entity.UserCredentials;
import com.jbmotos.model.repositories.UserCredentialsRepository;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceImplTest {

    @InjectMocks
    private AuthorizationServiceImpl authorizationService;

    @Mock
    private UserCredentialsRepository repository;

    @Test
    @DisplayName("Deve carregar usuário por login")
    void loadUserByUsername() {
        UserCredentials userCredentials = UserCredentials.builder()
                .id(1L)
                .login("admin")
                .password("123456")
                .build();

        when(repository.findByLogin("admin")).thenReturn(userCredentials);

        UserDetails result = authorizationService.loadUserByUsername("admin");

        assertEquals(userCredentials, result);
    }
}
