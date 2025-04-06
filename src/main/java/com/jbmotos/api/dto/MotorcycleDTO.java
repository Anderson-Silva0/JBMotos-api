package com.jbmotos.api.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.*;

import lombok.*;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class MotorcycleDTO {

    private Integer id;

    @NotBlank(message = "O campo Placa é obrigatório.")
    @Length(min = 8, max = 8, message = "O campo Placa deve ter 8 caracteres.")
    private String plate;

    @NotBlank(message = "O campo Marca é obrigatório.")
    @Length(min = 3, max = 30, message = "O campo Marca deve ter entre 3 e 30 caracteres.")
    private String brand;

    @NotBlank(message = "O campo Modelo é obrigatório.")
    @Length(min = 3, max = 30, message = "O campo Modelo deve ter entre 3 e 30 caracteres.")
    private String model;

    @NotNull(message = "O campo Ano é obrigatório.")
    @Min(value = 1900, message = "Informe um ano maior ou igual à 1900.")
    @Max(value = 9999, message = "O campo Ano tem no máximo 4 dígitos.")
    private Integer year;

    private String motorcycleStatus;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createdAt;

    @CPF(message = "CPF inválido ou não encontrado na base de dados da Receita Federal.")
    @NotBlank(message = "O campo CPF do Cliente é obrigatório.")
    @Length(min = 14, max = 14, message = "O campo CPF do Cliente deve ter 14 caracteres.")
    private String customerCpf;
}
