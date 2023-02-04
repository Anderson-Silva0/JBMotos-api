package com.example.jbmotos.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @OneToOne
    private Endereco endereco;
}