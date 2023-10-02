package com.jbmotos.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "servico", schema = "jbmotos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cpf_funcionario", referencedColumnName = "cpf")
    private Funcionario funcionario;

    @ManyToOne
    @JoinColumn(name = "id_moto", referencedColumnName = "id")
    private Moto moto;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_pedido", referencedColumnName = "id")
    private Pedido pedido;

    @Column(name = "data_hora_cadastro")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime dataHoraCadastro;

    @Column(name = "servicos_realizados", length = 300)
    private String servicosRealizados;

    @Column(name = "observacao", length = 300)
    private String observacao;

    @Column(name = "preco_mao_de_obra")
    private BigDecimal precoMaoDeObra;
}
