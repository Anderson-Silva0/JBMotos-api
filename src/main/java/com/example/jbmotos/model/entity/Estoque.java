package com.example.jbmotos.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table(name = "estoque", schema = "jbmotos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(mappedBy = "estoque")
    private Produto produto;

    @Column(name = "min_estoque")
    private Integer estoqueMinimo;

    @Column(name = "max_estoque")
    private Integer estoqueMaximo;

    @Column(name="quantidade")
    private Integer quantidade;
}
