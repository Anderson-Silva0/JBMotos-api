package com.example.jbmotos.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendaDTO {

    private Integer id;

    @NotBlank(message = "O campo Cliente é obrigatório.")
    @CPF(message = "Número do CPF inexistente.")
    private String cpf_cliente;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime data_hora;

    @NotBlank(message = "O campo Funcionário é obrigatório.")
    @CPF(message = "Número do CPF inexistente.")
    private String cpf_funcionario;

    @NotBlank(message = "O campo Observação é obrigatório.")
    @Length(max = 255, message = "O campo Observação tem no máximo 255 caracteres.")
    private String observacao;

    @NotBlank(message = "O campo Forma de Pagamento é obrigatório.")
    @Length(max = 50, message = "O campo Forma de Pagamento tem no máximo 50 caracteres.")
    private String forma_de_pagamento;
}
