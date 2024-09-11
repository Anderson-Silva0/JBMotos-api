package com.jbmotos.model.entity;

import com.jbmotos.model.enums.Situacao;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Entity
@Table(name = "produto", schema = "jbmotos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50)
    private String nome;

    private BigDecimal precoCusto;

    private BigDecimal precoVenda;

    @Column(length = 30)
    private String marca;

    @Enumerated(EnumType.STRING)
    private Situacao statusProduto;

    private LocalDateTime dataHoraCadastro;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_estoque", referencedColumnName = "id")
    private Estoque estoque;

    @ManyToOne
    @JoinColumn(name = "cnpj_fornecedor", referencedColumnName = "cnpj")
    private Fornecedor fornecedor;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    private List<ProdutoVenda> produtosVenda;

    @PrePersist
    public void prePersist() {
        this.dataHoraCadastro = LocalDateTime.now(ZoneId.of("America/Recife"));
    }
}
