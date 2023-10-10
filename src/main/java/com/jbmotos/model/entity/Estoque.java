package com.jbmotos.model.entity;

import jakarta.persistence.*;

import com.jbmotos.model.enums.StatusEstoque;

import lombok.*;

@Entity
@Table(name = "estoque", schema = "jbmotos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(mappedBy = "estoque")
    private Produto produto;

    private Integer estoqueMinimo;

    private Integer estoqueMaximo;

    private Integer quantidade;

    @Enumerated(EnumType.STRING)
    private StatusEstoque status;
}
