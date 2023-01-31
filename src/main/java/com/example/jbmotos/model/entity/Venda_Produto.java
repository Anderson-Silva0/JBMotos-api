package com.example.jbmotos.model.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class Venda_Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venda_produto")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "venda")
    private Venda venda;

    @ManyToOne
    @JoinColumn(name = "produto")
    private Produto produto;

    @NotBlank(message = "O campo Quantidade é obrigatório")
    private Integer quantidade;

    @NotBlank(message = "O campo Valor é obrigatório")
    private BigDecimal valor;
}
