package com.jbmotos.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

import lombok.*;

import org.hibernate.annotations.CreationTimestamp;

import com.jbmotos.model.enums.Situacao;

@Entity
@Table(name = "cliente", schema = "jbmotos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class Cliente {

    @Id
    @Column(length = 14, unique = true)
    private String cpf;

    @Column(length = 50)
    private String nome;

    @Column(length = 200, unique = true)
    private String email;

    @Column(length = 15)
    private String telefone;

    @Enumerated(EnumType.STRING)
    private Situacao statusCliente;

    @CreationTimestamp
    private LocalDateTime dataHoraCadastro;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_endereco", referencedColumnName = "id")
    private Endereco endereco;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Venda> vendas;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Moto> motos;
}