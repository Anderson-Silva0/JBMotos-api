package com.example.jbmotos.api.dto;

import com.example.jbmotos.model.entity.ProdutoPedido;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CNPJ;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProdutoDTO {

    private Integer id;

    @NotBlank(message = "O campo Nome é obrigatório.")
    @Length(min = 3,max = 50, message = "O campo Nome deve ter entre 3 e 50 caracteres.")
    private String nome;

    @NotNull(message = "O campo Preço de Custo é obrigatório.")
    private BigDecimal precoCusto;

    @NotNull(message = "O campo Preço de Venda é obrigatório.")
    private BigDecimal precoVenda;

    @NotBlank(message = "O campo Marca é obrigatório.")
    @Length(min = 3, max = 30, message = "O campo Marca deve ter entre 3 e 30 caracteres.")
    private String marca;

    @NotNull(message = "O Id do Estoque não pode ser nulo.")
    private Integer idEstoque;

    @NotBlank(message = "O campo CNPJ é obrigatório.")
    @Length(min = 18, max = 18, message = "O campo CNPJ deve ter 18 caracteres.")
    @CNPJ(message = "O CNPJ informado é inválido. Certifique-se de que está digitando corretamente e tente novamente.")
    private String cnpjFornecedor;

    @JsonIgnore
    private List<ProdutoPedido> produtosPedido;
}
