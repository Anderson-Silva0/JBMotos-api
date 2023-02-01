package com.example.jbmotos.model.entity;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.math.BigDecimal;
@Table(name = "produto", schema = "jbmotos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nome")
    @NotBlank(message = "O campo Nome é obrigatório")
    @Length(min = 3, max = 50, message = "O campo Nome deve ter entre 3 e 50 caracteres.")
    private String nome;

    @Column(name = "preco_venda")
    @NotBlank(message = "O campo Preço de Venda é obrigatório")
    private BigDecimal preco_venda;

    @Column(name = "min_estoque")
    @NotBlank(message = "O campo Estoque Mínimo é obrigatório")
    private Integer min_estoque;
}
