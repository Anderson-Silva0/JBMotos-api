package com.example.jbmotos.api.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Length(min = 3, max = 30, message = "O campo Marca deve ter entre 3 e 30 caracteres.")
    private String marca;

    @NotBlank(message = "O campo Modelo é obrigatório.")
    @Length(min = 3, max = 30, message = "O campo Modelo deve ter entre 3 e 30 caracteres.")
    private String modelo;

    @NotNull(message = "O campo Ano é obrigatório.")
    @Min(value = 1900, message = "Informe um ano maior ou igual à 1900.")
    @Max(value = 9999, message = "O campo Ano tem no máximo 4 dígitos.")
    private Integer ano;

    private String statusMoto;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataHoraCadastro;

    @CPF(message = "CPF inválido ou não encontrado na base de dados da Receita Federal.")
    @NotBlank(message = "O campo CPF do Cliente é obrigatório.")
    @Length(min = 14, max = 14, message = "O campo CPF do Cliente deve ter 14 caracteres.")
    private String cpfCliente;
}
