package com.example.jbmotos.model.entity;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CNPJ;

import javax.persistence.*;
import java.util.List;

@Table(name = "fornecedor", schema = "jbmotos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Fornecedor {

    @Id
    @Column(name = "cnpj", unique = true)
    @NotBlank(message = "O campo CNPJ é obrigatório")
    @Length(max = 18)
    @CNPJ
    private String cnpj;

    @Column(name = "nome")
    @NotBlank(message = "O campo Nome é obrigatório")
    @Length(max = 50)
    private String nome;

    @Column(name = "telefone")
    @NotBlank(message = "O campo Telefone é obrigatório")
    @Length(max = 15)
    private String telefone;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_endereco", referencedColumnName = "id")
    private Endereco endereco;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fornecedor")
    @ToString.Exclude
    @JsonIgnore
    private List<Produto> produtos;
}
