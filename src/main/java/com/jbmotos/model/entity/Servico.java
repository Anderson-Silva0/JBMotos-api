package com.jbmotos.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "servico", schema = "jbmotos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cpf_funcionario", referencedColumnName = "cpf")
    private Funcionario funcionario;

    @ManyToOne
    @JoinColumn(name = "id_moto", referencedColumnName = "id")
    private Moto moto;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_venda", referencedColumnName = "id")
    private Venda venda;

    @CreationTimestamp
    private LocalDateTime dataHoraCadastro;

    @Column(length = 300)
    private String servicosRealizados;

    @Column(length = 300)
    private String observacao;

    private BigDecimal precoMaoDeObra;
}
