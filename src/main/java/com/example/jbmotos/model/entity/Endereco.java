package com.example.jbmotos.model.entity;

import javax.validation.constraints.NotBlank;

import com.example.jbmotos.model.enums.TipoUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "endereco", schema = "jbmotos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "rua")
    @NotBlank(message = "O campo Rua é obrigatório")
    @Length(max = 100, message = "O campo Rua deve ter no máximo 100 caracteres.")
    private String rua;

    @Column(name = "cep")
    @NotBlank(message = "O campo CEP é obrigatório")
    @Length(max = 10, message = "O campo CEP deve ter no máximo 10 caracteres.")
    private String cep;

    @Column(name = "numero")
    @NotNull(message = "O campo Numero é obrigatório")
    private Integer numero;

    @Column(name = "cidade")
    @NotBlank(message = "O campo Cidade é obrigatório")
    @Length(max = 50, message = "O campo Cidade deve ter no máximo 50 caracteres.")
    private String cidade;

    @Column(name = "tipo_usuario")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "O campo Tipo do Usuário é obrigatório")
    @Length(max = 14)
    private TipoUsuario tipoUsuario;

    @Column(name = "cpf_usuario")
    @NotBlank(message = "O campo CPF do Usuário é obrigatório")
    @Length(max = 14)
    @CPF(message = "Número do CPF inválido")
    private String cpfUsuario;
}
