package com.example.jbmotos.model.entity;

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
import javax.persistence.Table;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import com.example.jbmotos.model.enums.Situacao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "moto", schema = "jbmotos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Moto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "placa", length = 8)
    private String placa;

    @Column(name = "marca", length = 30)
    private String marca;

    @Column(name = "modelo", length = 30)
    private String modelo;

    @Column(name = "ano")
    private Integer ano;

    @Column(name = "statusMoto")
    @Enumerated(EnumType.STRING)
    private Situacao statusMoto;

    @Column(name = "data_hora_cadastro")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime dataHoraCadastro;

    @ManyToOne
    @JoinColumn(name = "cpf_cliente", referencedColumnName = "cpf")
    private Cliente cliente;

    @OneToMany(mappedBy = "moto", cascade = CascadeType.ALL)
    private List<Servico> servicos;
 }
