package com.jbmotos.api.dto;

import java.time.LocalDateTime;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class CustomerDTO {

    @CPF(message = "CPF inválido ou não encontrado na base de dados da Receita Federal.")
    @NotBlank(groups = SaleDTO.SaleValidationGroup.class, message = "O campo CPF é obrigatório.")
    @Length(min = 14, max = 14, message = "O campo CPF deve ter 14 caracteres.")
    private String cpf;

    @NotBlank(message = "O campo Nome é obrigatório.")
    @Length(min = 3, max = 50, message = "O campo Nome deve ter entre 3 e 50 caracteres.")
    private String name;

    @NotBlank(message = "O campo Email é obrigatório.")
    @Length(min = 5, max = 200, message = "O campo Email deve ter entre 5 e 200 caracteres.")
    @Email(message = "O endereço de email informado é inválido.")
    private String email;

    @NotBlank(message = "O campo Telefone é obrigatório.")
    @Length(min = 15, max = 15, message = "O campo Telefone deve ter no mínimo 15 caracteres.")
    private String phone;

    private String customerStatus;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createdAt;

    @Valid
    private AddressDTO address;

}
