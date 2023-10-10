package com.jbmotos.model.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "produto_venda", schema = "jbmotos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ProdutoVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_venda")
    private Venda venda;

    @ManyToOne
    @JoinColumn(name = "id_produto")
    private Produto produto;

    private Integer quantidade;

    private BigDecimal valorUnidade;

    private BigDecimal valorTotal;
}
