package com.jbmotos.model.entity;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "endereco", schema = "jbmotos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100)
    private String rua;

    @Column(length = 9)
    private String cep;

    private Integer numero;

    @Column(length = 50)
    private String bairro;

    @Column(length = 50)
    private String cidade;

    @OneToOne(mappedBy = "endereco")
    private Cliente cliente;

    @OneToOne(mappedBy = "endereco")
    private Funcionario funcionario;

    @OneToOne(mappedBy = "endereco")
    private Fornecedor fornecedor;
}
