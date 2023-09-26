package com.example.jbmotos.api.dto;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import com.example.jbmotos.model.entity.ProdutoPedido;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PedidoDTO {

    private Integer id;

    @NotBlank(message = "O campo CPF do Cliente é obrigatório.")
    @Length(min = 14, max = 14, message = "O campo CPF do Cliente deve ter 14 caracteres.")
    @CPF(message = "CPF do Cliente inválido ou não encontrado na base de dados da Receita Federal.")
    private String cpfCliente;

    @NotBlank(message = "O campo CPF do Funcionário é obrigatório.")
    @Length(min = 14, max = 14, message = "O campo CPF do Funcionário deve ter 14 caracteres.")
    @CPF(message = "CPF do Fucionário inválido ou não encontrado na base de dados da Receita Federal.")
    private String cpfFuncionario;

    @Length(max = 100, message = "O campo Observação tem no máximo 100 caracteres.")
    private String observacao;

    @NotBlank(message = "O campo Forma de Pagamento é obrigatório.")
    @Length(max = 50, message = "O campo Forma de Pagamento tem no máximo 50 caracteres.")
    private String formaDePagamento;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataHoraCadastro;

    @JsonIgnore
    private List<ProdutoPedido> produtosPedidos;
}
