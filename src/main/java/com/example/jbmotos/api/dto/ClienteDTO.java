package com.example.jbmotos.api.dto;

import com.example.jbmotos.model.entity.Endereco;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClienteDTO {

    @CPF(message = "Número do CPF inválido")
    private String cpf;

    @NotBlank(message = "O campo Nome é obrigatório")
    @Length(min = 3,max = 50, message = "O campo Nome deve ter entre 3 e 50 caracteres")
    private String nome;

    @NotBlank(message = "O campo Email é obrigatório")
    @Email(message = "Endereço de E-mail inválido")
    private String email;

    @NotBlank(message = "O campo Telefone é obrigatório")
    @Length(min = 9, max = 15, message = "O campo Telefone deve ter entre 9 e 15 caracteres.")
    private String telefone;

    @NotBlank(message = "O campo Endereço é obrigatório")
    private Integer endereco;
}
