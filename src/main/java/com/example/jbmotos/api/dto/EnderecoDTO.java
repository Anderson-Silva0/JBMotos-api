package com.example.jbmotos.api.dto;

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
public class EnderecoDTO {

    private Integer id;

    @NotBlank(message = "O campo Rua é obrigatório")
    @Length(max = 100, message = "O campo Rua deve ter no máximo 100 caracteres.")
    private String rua;

    @NotBlank(message = "O campo CEP é obrigatório")
    @Length(max = 10, message = "O campo CEP deve ter no máximo 10 caracteres.")
    private String cep;

    @NotNull(message = "O campo Numero é obrigatório")
    private Integer numero;

    @NotBlank(message = "O campo Cidade é obrigatório")
    @Length(min =  4, max = 50, message = "O campo Cidade deve ter no mínimo 4 e no máximo 50 caracteres.")
    private String cidade;

    @NotNull(message = "O campo Tipo do Usuário é obrigatório")
    private String tipoUsuario;

    @NotBlank(message = "O campo CPF do Usuário é obrigatório")
    @CPF(message = "Número do CPF inválido")
    private String cpfUsuario;
}
