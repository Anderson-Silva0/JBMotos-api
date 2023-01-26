package com.example.jbmotos.model.entity;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CNPJ;

import javax.persistence.*;

@Table(name = "fornecedor", schema = "jbmotos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fornecedor")
    private Integer id;

    @Column(name = "nome")
    @NotBlank(message = "O campo Nome é obrigatório")
    private String nome;

    @Column
    @NotBlank(message = "O campo CNPJ é obrigatório")
    @Length(min = 14, max = 18)
    @CNPJ
    private String cnpj;

}
