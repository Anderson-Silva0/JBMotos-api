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
    @Column(name = "cnpj_fornecedor")
    @NotBlank(message = "O campo CNPJ é obrigatório")
    @CNPJ
    private String cnpj;

    @Column(name = "nome")
    @NotBlank(message = "O campo Nome é obrigatório")
    @Length(min = 3, max = 50, message = "O campo Nome deve ter entre 3 e 50 caracteres.")
    private String nome;

    @Column(name = "telefone")
    @NotBlank(message = "O campo Telefone é obrigatório")
    @Length(min =11, max = 15, message = "O campo Telefone deve ter entre 11 e 15 caracteres.")
    private String telefone;
}
