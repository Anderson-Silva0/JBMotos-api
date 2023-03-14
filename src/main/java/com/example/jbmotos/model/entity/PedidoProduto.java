package com.example.jbmotos.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Table(name = "pedido_produto", schema = "jbmotos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class PedidoProduto {

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

    private Integer quantidade;

    private BigDecimal valor;
}
