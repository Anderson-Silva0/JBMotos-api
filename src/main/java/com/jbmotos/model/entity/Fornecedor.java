package com.jbmotos.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import com.jbmotos.model.enums.Situacao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "fornecedor", schema = "jbmotos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Fornecedor {

    @Id
    @Column(name = "cnpj", length = 18)
    private String cnpj;

    @Column(name = "nome", length = 50)
    private String nome;

    @Column(name = "telefone", length = 15)
    private String telefone;

    @Column(name = "statusFornecedor")
    @Enumerated(EnumType.STRING)
    private Situacao statusFornecedor;

    @Column(name = "data_hora_cadastro")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime dataHoraCadastro;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_endereco", referencedColumnName = "id")
    private Endereco endereco;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fornecedor")
    private List<Produto> produtos;
}
