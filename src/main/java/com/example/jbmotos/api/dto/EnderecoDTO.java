package com.example.jbmotos.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnderecoDTO {

    private Integer id;

    @NotBlank(message = "O campo Rua é obrigatório.")
    @Length(min = 3, max = 100, message = "O campo Rua deve ter entre 3 e 100 caracteres.")
    private String rua;

    @NotBlank(message = "O campo CEP é obrigatório.")
    @Length(min = 9, max = 9, message = "O campo CEP deve ter 9 caracteres.")
    @Pattern(regexp = "^\\d{5}-\\d{3}$", message = "Formato de CEP inválido.")
    private String cep;

    @NotNull(message = "O campo Numero é obrigatório.")
    private Integer numero;

    @NotBlank(message = "O campo Bairro é obrigatório.")
    @Length(min =  3, max = 50, message = "O campo Bairro deve ter entre 3 e 50 caracteres.")
    private String bairro;

    @NotBlank(message = "O campo Cidade é obrigatório.")
    @Length(min =  3, max = 50, message = "O campo Cidade deve ter entre 3 e 50 caracteres.")
    private String cidade;
}
