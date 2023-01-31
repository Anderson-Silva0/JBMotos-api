package com.example.jbmotos.api.dto;

import com.example.jbmotos.model.entity.Fornecedor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class ProdutoDTO {

    private Integer id;

    @NotBlank(message = "O campo Nome é obrigatório")
    @Length(min = 3, max = 50, message = "O campo Nome deve ter entre 3 e 50 caracteres.")
    private String nome;

    @NotBlank(message = "O campo Valor é obrigatório")
    private BigDecimal valor;

    @NotBlank(message = "O campo Fornecedor é obrigatório")
    private String fornecedor;
}
