package com.example.jbmotos.api.dto;

import com.example.jbmotos.model.entity.Cliente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

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

    @NotNull(message = "O Ano é obrigatório.")
    private Integer ano;

    @CPF(message = "Número do CPF inexistente.")
    @NotBlank(message = "O campo CPF do Cliente é obrigatório.")
    private String cpfCliente;
}
