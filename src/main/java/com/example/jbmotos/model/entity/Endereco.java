package com.example.jbmotos.model.entity;

import lombok.*;

import javax.persistence.*;

@Table(name = "endereco", schema = "jbmotos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "rua", length = 100)
    private String rua;

    @Column(name = "cep", length = 9)
    private String cep;

    @Column(name = "numero")
    private Integer numero;

    @Column(name = "bairro", length = 50)
    private String bairro;

    @Column(name = "cidade", length = 50)
    private String cidade;

    @OneToOne(mappedBy = "endereco")
    private Cliente cliente;

    @OneToOne(mappedBy = "endereco")
    private Funcionario funcionario;

    @OneToOne(mappedBy = "endereco")
    private Fornecedor fornecedor;
}
