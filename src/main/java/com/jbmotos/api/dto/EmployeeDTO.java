package com.jbmotos.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jbmotos.api.validation.ValidationGroups;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class EmployeeDTO {

    @NotBlank(groups = ValidationGroups.CpfValidationGroup.class,
            message = "O campo CPF do Funcionário é obrigatório.")
    @Length(groups = ValidationGroups.CpfValidationGroup.class,
            min = 14, max = 14, message = "O campo CPF do Funcionário deve ter 14 caracteres.")
    @CPF(groups = ValidationGroups.CpfValidationGroup.class,
            message = "CPF inválido ou não encontrado na base de dados da Receita Federal.")
    private String cpf;

    @NotBlank(message = "O campo Nome é obrigatório.")
    @Length(min = 3, max = 50, message = "O campo Nome deve ter entre 3 e 50 caracteres.")
    private String name;

    @NotBlank(message = "O campo Telefone é obrigatório.")
    @Length(min = 15, max = 15, message = "O campo Telefone deve ter no mínimo 15 caracteres.")
    private String phone;

    private String employeeStatus;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createdAt;

    @Valid
    private AddressDTO address;

}
