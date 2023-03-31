package com.example.jbmotos.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MotoDTO {

    private Integer id;

    @NotBlank(message = "O campo Placa é obrigatório.")
    @Length(min = 8, max = 8, message = "O campo Placa deve ter 8 caracteres.")
    private String placa;

    @NotBlank(message = "O campo Marca é obrigatório.")
    private String marca;

    @NotBlank(message = "O campo Modelo é obrigatório.")
    private String modelo;

    @NotNull(message = "O campo Ano é obrigatório.")
    @Min(value = 1900, message = "Informe um ano maior ou igual à 1900.")
    @Max(value = 9999, message = "O campo Ano tem no máximo 4 dígitos.")
    private Integer ano;

    @CPF(message = "Número do CPF inexistente.")
    @NotBlank(message = "O campo CPF do Cliente é obrigatório.")
    private String cpfCliente;
}
