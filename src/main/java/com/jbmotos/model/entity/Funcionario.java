package com.jbmotos.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

import lombok.*;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import com.jbmotos.model.enums.Situacao;

@Entity
@Table(name = "funcionario", schema = "jbmotos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class Funcionario {

    @Id
    @Column(length = 14, unique = true)
    private String cpf;

    @Column(length = 50)
    private String nome;

    @Column(length = 15)
    private String telefone;

    @Enumerated(EnumType.STRING)
    private Situacao statusFuncionario;

    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime dataHoraCadastro;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_endereco", referencedColumnName = "id")
    private Endereco endereco;

    @OneToMany(mappedBy = "funcionario", cascade = CascadeType.ALL)
    private List<Venda> vendas;

    @OneToMany(mappedBy = "funcionario", cascade = CascadeType.ALL)
    private List<Servico> servicos;
}
