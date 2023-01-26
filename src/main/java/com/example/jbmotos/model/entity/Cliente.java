package com.example.jbmotos.model.entity;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import javax.persistence.*;

@Table(name = "cliente", schema = "jbmotos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Cliente {

    @Id
    @Column(name = "cpf_cliente", unique = true)
    @CPF
    private String cpf;

    @Column(name = "nome")
    @NotBlank(message = "O campo Nome é obrigatório")
    @Length(min = 3,max = 50)
    private String nome;

    @Column(name = "email", unique = true)
    @NotBlank(message = "O campo Email é obrigatório")
    @Length(min =10, max = 50)
    @Email
    private String email;

    @Column(name = "telefone")
    @NotBlank(message = "O campo Telefone é obrigatório")
    @Length(min =11, max = 15)
    private String telefone;

    @NotBlank(message = "O campo Endereco é obrigatório")
    @ManyToOne
    @JoinColumn(name = "id_endereco")
    private Endereco endereco;
}