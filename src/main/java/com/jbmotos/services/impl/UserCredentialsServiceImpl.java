package com.jbmotos.services.impl;

import com.jbmotos.api.dto.AuthenticationDTO;
import com.jbmotos.api.dto.UserCredentialsDTO;
import com.jbmotos.model.entity.UserCredentials;
import com.jbmotos.model.entity.Employee;
import com.jbmotos.model.enums.Situation;
import com.jbmotos.model.repositories.UserCredentialsRepository;
import com.jbmotos.services.UserCredentialsService;
import com.jbmotos.services.EmployeeService;
import com.jbmotos.services.exception.AuthenticationException;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserCredentialsServiceImpl implements UserCredentialsService {

    private final AuthenticationManager authenticationManager;
    private final UserCredentialsRepository repository;
    private final EmployeeService employeeService;
    private final ModelMapper mapper;

    public UserCredentialsServiceImpl(AuthenticationManager authenticationManager,
                                     UserCredentialsRepository repository,
                                     EmployeeService employeeService,
                                     ModelMapper mapper) {
        this.authenticationManager = authenticationManager;
        this.repository = repository;
        this.employeeService = employeeService;
        this.mapper = mapper;
    }

    @Override
    public Authentication login(AuthenticationDTO dto) {
        try {
            UserCredentials credentials = this.repository.findCredentialsByLogin(dto.login());

            var usernamePassword = new UsernamePasswordAuthenticationToken(dto.login(), dto.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);

            Employee employee = credentials.getEmployee();
            if (employee.getEmployeeStatus() == Situation.ACTIVE) {
                return auth;
            } else {
                throw new AuthenticationException("Não é possível fazer login. Usuário inativo.");
            }
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new AuthenticationException("Login ou senha inválidos");
        }
    }

    @Override
    @Transactional
    public void registerUser(UserCredentialsDTO dto) {
        if (this.repository.findByLogin(dto.getLogin()) != null) {
            throw new AuthenticationException("O login já existe");
        }

        Employee employeeSaved = this.employeeService.saveEmployee(dto.getEmployee());

        UserCredentials userMap = this.mapper.map(dto, UserCredentials.class);
        String encryptedPassword = new BCryptPasswordEncoder().encode(userMap.getPassword());
        userMap.setPassword(encryptedPassword);
        userMap.setEmployee(employeeSaved);

        this.repository.save(userMap);
    }

}
