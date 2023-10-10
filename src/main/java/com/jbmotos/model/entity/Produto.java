package com.jbmotos.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

import lombok.*;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import com.jbmotos.model.enums.Situacao;

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

    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime dataHoraCadastro;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_estoque", referencedColumnName = "id")
    private Estoque estoque;

    @ManyToOne
    @JoinColumn(name = "cnpj_fornecedor", referencedColumnName = "cnpj")
    private Fornecedor fornecedor;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    private List<ProdutoVenda> produtosVenda;
}
