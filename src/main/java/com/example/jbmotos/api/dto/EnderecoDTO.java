package com.example.jbmotos.api.dto;

import com.example.jbmotos.model.enums.TipoUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.NotBlank;

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

    @NotBlank(message = "O campo Numero é obrigatório")
    private Integer numero;

    @NotBlank(message = "O campo Cidade é obrigatório")
    @Length(max = 50, message = "O campo Cidade deve ter no máximo 50 caracteres.")
    private String cidade;

    @NotBlank(message = "O campo Tipo do Usuário é obrigatório")
    private String tipo_usuario;

    @NotBlank(message = "O campo CPF do Usuário é obrigatório")
    @CPF
    private String cpf_usuario;
}
