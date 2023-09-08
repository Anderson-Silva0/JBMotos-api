package com.example.jbmotos.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import com.example.jbmotos.model.enums.Situacao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "statusProduto")
    @Enumerated(EnumType.STRING)
    private Situacao statusProduto;

    @Column(name = "data_hora_cadastro")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime dataHoraCadastro;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_estoque", referencedColumnName = "id")
    private Estoque estoque;

    @ManyToOne
    @JoinColumn(name = "cnpj_fornecedor", referencedColumnName = "cnpj")
    private Fornecedor fornecedor;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    private List<ProdutoPedido> produtosPedido;
}
