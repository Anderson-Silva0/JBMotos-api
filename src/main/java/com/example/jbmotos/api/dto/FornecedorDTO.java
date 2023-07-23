package com.example.jbmotos.api.dto;

import com.example.jbmotos.model.repositories.FornecedorRepository;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CNPJ;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FornecedorDTO {

    @Null(groups = {FornecedorRepository.class})
    @NotBlank(message = "O campo CNPJ é obrigatório.")
    @Length(min = 18, max = 18, message = "O campo CNPJ deve ter 18 caracteres.")
    @CNPJ(message = "O CNPJ informado é inválido. Certifique-se de que está digitando corretamente e tente novamente.")
    private String cnpj;

    @NotBlank(groups = {FornecedorRepository.class}, message = "O campo Nome é obrigatório.")
    @NotBlank(message = "O campo Nome é obrigatório.")
    @Length(min = 3, max = 50, message = "O campo Nome deve ter entre 3 e 50 caracteres.")
    private String nome;

    @NotBlank(groups = {FornecedorRepository.class}, message = "O campo Telefone é obrigatório.")
    @NotBlank(message = "O campo Telefone é obrigatório.")
    @Length(min = 15, max = 15, message = "O campo Celular deve ter no mínimo 15 caracteres.")
    private String telefone;

    private String statusFornecedor;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataHoraCadastro;

    @NotNull(groups = {FornecedorRepository.class}, message = "O Endereço do Fornecedor não pode ser nulo.")
    @NotNull(message = "O Endereço do Fornecedor não pode ser nulo.")
    private Integer endereco;
}
