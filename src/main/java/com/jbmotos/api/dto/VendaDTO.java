package com.jbmotos.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class VendaDTO {

    private Integer id;

    private ClienteDTO cliente;

    private FuncionarioDTO funcionario;

    @Length(max = 100, message = "O campo Observação tem no máximo 100 caracteres.")
    private String observacao;

    @NotBlank(message = "O campo Forma de Pagamento é obrigatório.")
    @Length(max = 50, message = "O campo Forma de Pagamento tem no máximo 50 caracteres.")
    private String formaDePagamento;
    
    @Valid
    private PagamentoCartaoDTO pagamentoCartao;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataHoraCadastro;

    private BigDecimal valorTotalVenda;

    private List<ProdutoVendaDTO> produtosVenda;
}
