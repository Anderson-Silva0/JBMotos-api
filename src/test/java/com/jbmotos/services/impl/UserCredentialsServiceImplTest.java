package com.jbmotos.services.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.jbmotos.api.dto.AuthenticationDTO;
import com.jbmotos.api.dto.UserCredentialsDTO;
import com.jbmotos.model.entity.Employee;
import com.jbmotos.model.entity.UserCredentials;
import com.jbmotos.model.enums.Role;
import com.jbmotos.model.enums.Situation;
import com.jbmotos.model.repositories.UserCredentialsRepository;
import com.jbmotos.services.EmployeeService;
import com.jbmotos.services.exception.AuthenticationException;

@ExtendWith(MockitoExtension.class)
class UserCredentialsServiceImplTest {

    @InjectMocks
    private UserCredentialsServiceImpl service;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserCredentialsRepository repository;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private ModelMapper mapper;

    private UserCredentials userCredentials;
    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = Employee.builder().cpf("123.456.789-10").name("Func").employeeStatus(Situation.ACTIVE).build();
        userCredentials = UserCredentials.builder().id(1L).login("admin").password("123456").role(Role.ADMIN).employee(employee).build();
    }

    @Test
    @DisplayName("Deve autenticar usuário ativo")
    void login() {
        AuthenticationDTO dto = new AuthenticationDTO("admin", "123456");
        Authentication auth = new UsernamePasswordAuthenticationToken("admin", "123456");

        when(repository.findCredentialsByLogin("admin")).thenReturn(userCredentials);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);

        Authentication result = service.login(dto);

        assertNotNull(result);
        assertEquals(auth, result);
    }

    @Test
    @DisplayName("Deve rejeitar login inválido")
    void loginInvalid() {
        AuthenticationDTO dto = new AuthenticationDTO("admin", "123456");
        when(repository.findCredentialsByLogin("admin")).thenThrow(new org.springframework.security.core.AuthenticationException("bad") {
        });

        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> service.login(dto));
        assertEquals("Login ou senha inválidos", exception.getMessage());
    }

    @Test
    @DisplayName("Deve registrar novo usuário")
    void registerUser() {
        UserCredentialsDTO dto = UserCredentialsDTO.builder()
                .login("admin")
                .password("123456")
                .role("ADMIN")
                .employee(new com.jbmotos.api.dto.EmployeeDTO())
                .build();

        UserCredentials mapped = UserCredentials.builder().login("admin").password("123456").role(Role.ADMIN).employee(employee).build();

        when(repository.findByLogin("admin")).thenReturn(null);
        when(employeeService.saveEmployee(dto.getEmployee())).thenReturn(employee);
        when(mapper.map(dto, UserCredentials.class)).thenReturn(mapped);
        when(repository.save(any(UserCredentials.class))).thenReturn(mapped);

        assertDoesNotThrow(() -> service.registerUser(dto));
    }
}
