package com.example.jbmotos.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "pedido", schema = "jbmotos")
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

    @ManyToOne
    @JoinColumn(name = "cpf_cliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "cpf_funcionario")
    private Funcionario funcionario;

    @Column(name = "observacao", length = 300)
    private String observacao;

    @Column(name = "forma_de_pagamento", length = 50)
    private String formaDePagamento;

    @Column(name = "data_hora_cadastro")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime dataHoraCadastro;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<ProdutoPedido> produtosPedido;

    @OneToOne(mappedBy = "pedido")
    private Servico servico;
}