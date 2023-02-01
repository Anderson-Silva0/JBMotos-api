package com.example.jbmotos.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CNPJ;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Table(name = "fornecedor_produto", schema = "jbmotos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Fornecedor_Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @JoinColumn(name = "cnpj_fornecedor")
    @NotBlank(message = "O campo CNPJ é obrigatório")
    @CNPJ
    private String cnpj_fornecedor;

    @JoinColumn(name = "id_produto")
    @NotBlank(message = "O campo Código do Produto é obrigatório")
    private Integer id_produto;

    @Column(name = "qtd_estoque")
    @NotBlank(message = "O campo Quantidade do Estoque é obrigatório")
    private Integer qtd_estoque;

    @Column(name = "preco_custo")
    @NotBlank(message = "O campo Preço do Custo é obrigatório")
    private BigDecimal preco_custo;
}
