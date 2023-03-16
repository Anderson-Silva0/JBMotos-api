package com.example.jbmotos.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Table(name = "produto_pedido", schema = "jbmotos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ProdutoPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_pedido")
    @JsonIgnore
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "id_produto")
    @JsonIgnore
    private Produto produto;

    @Column(name = "quantidade")
    private Integer quantidade;

    @Column(name = "valor_unidade")
    private BigDecimal valorUnidade;

    @Column(name = "valorTotal")
    private BigDecimal valorTotal;
}
