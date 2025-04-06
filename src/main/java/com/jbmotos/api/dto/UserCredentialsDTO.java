package com.jbmotos.api.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class UserCredentialsDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O campo Login é obrigatório.")
    @Length(min = 4, max = 50, message = "O campo Login deve ter entre 5 e 50 caracteres.")
    private String login;

    @NotBlank(message = "O campo Senha é obrigatório.")
    @Length(min = 6, max = 15, message = "O campo Senha deve ter entre 6 e 15 caracteres.")
    private String password;

    @NotBlank(message = "O campo Permissão é obrigatório.")
    private String role;

    @Valid
    private EmployeeDTO employee;

}
