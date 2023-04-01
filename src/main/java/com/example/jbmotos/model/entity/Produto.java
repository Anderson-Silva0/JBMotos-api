package com.example.jbmotos.model.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Table(name = "produto", schema = "jbmotos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nome", length = 50)
    private String nome;

    @Column(name = "preco_custo")
    private BigDecimal precoCusto;

    @Column(name = "preco_venda")
    private BigDecimal precoVenda;

    @Column(name = "marca", length = 30)
    private String marca;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_estoque", referencedColumnName = "id")
    private Estoque estoque;

    @ManyToOne
    @JoinColumn(name = "cnpj_fornecedor", referencedColumnName = "cnpj")
    private Fornecedor fornecedor;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    private List<ProdutoPedido> produtosPedido;
}
