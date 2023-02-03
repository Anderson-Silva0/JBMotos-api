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

@Table(name = "cliente", schema = "jbmotos", uniqueConstraints = {@UniqueConstraint(columnNames = {"cpf"})})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Cliente {

    @Id
    @Column(name = "cpf")
    private String cpf;

    @Column(name = "nome")
    private String nome;

    @Column(name = "email")
    private String email;

    @Column(name = "telefone")
    private String telefone;

    @JoinColumn(name = "id_endereco")
    @OneToOne(targetEntity = Endereco.class)
    private Endereco endereco;
}