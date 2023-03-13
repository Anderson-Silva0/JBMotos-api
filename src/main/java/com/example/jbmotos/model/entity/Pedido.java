package com.example.jbmotos.model.entity;

import lombok.*;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Table(name = "venda", schema = "jbmotos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST})
    @JoinColumn(name = "cpf_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST})
    @JoinColumn(name = "cpf_funcionario", nullable = false)
    private Funcionario funcionario;

    @Column(name = "data_hora")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime dataHora;

    @Column(name = "observacao")
    private String observacao;

    @Column(name = "forma_de_pagamento")
    private String formaDePagamento;

//    @ManyToMany(mappedBy = "vendas", cascade = CascadeType.ALL)
//    private Set<Produto> produtos;
}