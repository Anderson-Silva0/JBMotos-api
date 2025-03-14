package com.jbmotos.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Entity
@Table(name = "venda", schema = "jbmotos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cpf_cliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "cpf_funcionario")
    private Funcionario funcionario;

    @Column(length = 100)
    private String observacao;

    @Column(length = 50)
    private String formaDePagamento;

    private LocalDateTime dataHoraCadastro;

    @OneToMany(mappedBy = "venda", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
    private List<ProdutoVenda> produtosVenda;

    @OneToOne(mappedBy = "venda")
    private Servico servico;

    @OneToOne(mappedBy = "venda", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
    private PagamentoCartao pagamentoCartao;

    private BigDecimal valorTotalVenda;

    @PrePersist
    public void prePersist() {
        this.dataHoraCadastro = LocalDateTime.now(ZoneId.of("America/Recife"));
    }
}