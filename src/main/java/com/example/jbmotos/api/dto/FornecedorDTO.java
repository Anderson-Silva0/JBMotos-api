package com.example.jbmotos.api.dto;

import com.example.jbmotos.model.repositories.FornecedorRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CNPJ;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FornecedorDTO {

    @Null(groups = {FornecedorRepository.class})
    @NotBlank(message = "O campo CNPJ é obrigatório")
    @Length(min = 14, max = 18, message = "O campo CNPJ deve ter no mínimo 14 e máximo 18 caracteres.")
    @CNPJ(message = "Número do CNPJ inexistente.")
    private String cnpj;

    @NotBlank(groups = {FornecedorRepository.class}, message = "O campo Nome é obrigatório")
    @NotBlank(message = "O campo Nome é obrigatório")
    @Length(min = 3, max = 50, message = "O campo Nome deve ter entre 3 e 50 caracteres.")
    private String nome;

    @NotBlank(groups = {FornecedorRepository.class}, message = "O campo Telefone é obrigatório")
    @NotBlank(message = "O campo Telefone é obrigatório")
    @Length(min =11, max = 15, message = "O campo Telefone deve ter entre 11 e 15 caracteres.")
    private String telefone;

    @NotNull(groups = {FornecedorRepository.class}, message = "O Endereço está inválido.")
    @NotNull(message = "O Endereço está inválido.")
    private Integer endereco;
}
