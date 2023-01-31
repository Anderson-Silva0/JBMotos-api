package com.example.jbmotos.model.entity;


import com.example.jbmotos.model.repositories.VendaRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Table(name = "venda", schema = "jbmotos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venda")
    private Integer id;

    @NotBlank(message = "O campo Cliente é obrigatório")
    @ManyToOne
    @JoinColumn(name = "cliente")
    private Cliente cliente;

    @Column(name = "data_hora")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime data_hora;

    @NotBlank(message = "O campo Funcionário é obrigatório")
    @ManyToOne
    @JoinColumn(name = "funcionario")
    private Funcionario funcionario;

    @Column(name = "observacao")
    @NotBlank(message = "O campo Observação é obrigatório")
    @Length(max = 255, message = "O campo Observação tem no máximo 255 caracteres.")
    private String observacao;

    @Column(name = "forma_de_pagamento")
    @NotBlank(message = "O campo Forma de Pagamento é obrigatório")
    @Length(max = 50, message = "O campo Forma de Pagamento tem no máximo 50 caracteres.")
    private String forma_de_pagamento;
}