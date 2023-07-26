package com.example.jbmotos.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClienteDTO {

    @CPF(message = "CPF inválido ou não encontrado na base de dados da Receita Federal.")
    @NotBlank(message = "O campo CPF é obrigatório.")
    @Length(min = 14, max = 14, message = "O campo CPF deve ter 14 caracteres.")
    private String cpf;

    @NotBlank(message = "O campo Nome é obrigatório.")
    @Length(min = 3, max = 50, message = "O campo Nome deve ter entre 3 e 50 caracteres.")
    private String nome;

    @NotBlank(message = "O campo Email é obrigatório.")
    @Length(min = 5, max = 200, message = "O campo Email deve ter entre 5 e 200 caracteres.")
    @Email(message = "O endereço de email informado é inválido.")
    private String email;

    @NotBlank(message = "O campo Telefone é obrigatório.")
    @Length(min = 15, max = 15, message = "O campo Celular deve ter no mínimo 15 caracteres.")
    private String telefone;

    private String statusCliente;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataHoraCadastro;

    @NotNull(message = "O Endereço do Cliente não pode ser nulo.")
    private Integer endereco;
}
