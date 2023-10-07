package com.jbmotos.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
public class ServicoDTO {

    private Integer id;

    @NotBlank(message = "O campo CPF do Funcionário é obrigatório.")
    @Length(min = 14, max = 14, message = "O campo CPF do Funcionário deve ter 14 caracteres.")
    @CPF(message = "CPF do Funcionário inválido ou não encontrado na base de dados da Receita Federal.")
    private String cpfFuncionario;

    @NotNull(message = "O Id da Moto não pode ser nulo.")
    private Integer idMoto;

    @NotNull(message = "O Id da Venda não pode ser nulo.")
    private Integer idVenda;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataHoraCadastro;

    @Length(max = 300, message = "O campo Serviços Realizados tem no máximo 300 caracteres.")
    private String servicosRealizados;

    @Length(max = 300, message = "O campo Observação tem no máximo 300 caracteres.")
    private String observacao;

    @NotNull(message = "O campo Preço de Mão de Obra não pode ser nulo.")
    private BigDecimal precoMaoDeObra;
}
