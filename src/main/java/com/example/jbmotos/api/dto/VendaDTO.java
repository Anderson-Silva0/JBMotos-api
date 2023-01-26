package com.example.jbmotos.api.dto;

import com.example.jbmotos.model.entity.Cliente;
import com.example.jbmotos.model.entity.Funcionario;
import com.example.jbmotos.model.entity.Produto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendaDTO {

    private Integer id;

    @NotBlank(message = "O campo Cliente é obrigatório")
    private String cliente;

    private List<Produto> produtos;

    private LocalDateTime data_hora;

    @Column(name = "quantidade")
    @NotBlank(message = "O campo Quantidade é obrigatório")
    private Integer quantidade;

    private String funcionario;

    @NotBlank(message = "O campo Observação é obrigatório")
    @Length(max = 255)
    private String observacao;

    @NotBlank(message = "O campo Forma de Pagamento é obrigatório")
    @Length(max = 50)
    private String forma_de_pagamento;

    @NotBlank(message = "O campo Valor é obrigatório")
    private BigDecimal valor;
}
