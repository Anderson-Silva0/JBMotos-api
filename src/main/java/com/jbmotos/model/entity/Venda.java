package com.jbmotos.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

import lombok.*;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

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

    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime dataHoraCadastro;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL)
    private List<ProdutoVenda> produtosVenda;

    @OneToOne(mappedBy = "venda")
    private Servico servico;
}