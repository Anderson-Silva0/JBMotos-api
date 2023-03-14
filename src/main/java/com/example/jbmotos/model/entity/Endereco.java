package com.example.jbmotos.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Column(name = "id", unique = true)
    private Integer id;

    @Column(name = "rua")
    private String rua;

    @Column(name = "cep")
    private String cep;

    @Column(name = "numero")
    private Integer numero;

    @Column(name = "bairro")
    private String bairro;

    @Column(name = "cidade")
    private String cidade;

    @OneToOne(mappedBy = "endereco")
    @ToString.Exclude
    @JsonIgnore
    private Cliente cliente;

    @OneToOne(mappedBy = "endereco")
    @ToString.Exclude
    @JsonIgnore
    private Funcionario funcionario;

    @OneToOne(mappedBy = "endereco")
    @ToString.Exclude
    @JsonIgnore
    private Fornecedor fornecedor;
}
