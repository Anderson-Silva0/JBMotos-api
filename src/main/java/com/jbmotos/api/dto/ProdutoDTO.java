package com.jbmotos.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jbmotos.model.entity.ProdutoPedido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @DecimalMin(value = "0.01", inclusive = false, message = "O campo Preço de Custo deve ser maior que zero.")
    private BigDecimal precoCusto;

    @NotNull(message = "O campo Preço de Venda é obrigatório.")
    @DecimalMin(value = "0.01", inclusive = false, message = "O campo Preço de Venda deve ser maior que zero.")
    private BigDecimal precoVenda;

    @NotBlank(message = "O campo Marca é obrigatório.")
    @Length(min = 3, max = 30, message = "O campo Marca deve ter entre 3 e 30 caracteres.")
    private String marca;

    private String statusProduto;

    @NotNull(message = "O Id do Estoque não pode ser nulo.")
    private Integer idEstoque;

    @NotBlank(message = "O campo CNPJ é obrigatório.")
    private String cnpjFornecedor;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataHoraCadastro;

    @JsonIgnore
    private List<ProdutoPedido> produtosPedido;
}
