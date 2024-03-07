package com.jbmotos.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.*;

import lombok.*;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ServicoDTO {

    private Integer id;

    @NotBlank(message = "O campo CPF do Funcionário é obrigatório.")
    private String cpfFuncionario;

    private Integer idMoto;

    private Integer idVenda;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataHoraCadastro;

    @Length(min = 5, max = 300, message = "O campo Serviços Realizados deve ter entre 5 e 300 caracteres.")
    private String servicosRealizados;

    @Length(max = 300, message = "O campo Observação deve ter no máximo 300 caracteres.")
    private String observacao;

    @DecimalMin(value = "0.01", inclusive = false, message = "O campo Preço de Mão de Obra deve ser maior que zero.")
    private BigDecimal precoMaoDeObra;
}
