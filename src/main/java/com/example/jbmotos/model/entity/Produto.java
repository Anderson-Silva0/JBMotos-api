package com.example.jbmotos.model.entity;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Column(name = "id_produto")
    private Integer id;

    @Column(name = "nome")
    @NotBlank(message = "O campo Nome é obrigatório")
    private String nome;

    @Column(name = "valor")
    @NotBlank(message = "O campo Valor é obrigatório")
    private BigDecimal valor;

    @NotBlank(message = "O campo Fornecedor é obrigatório")
    @ManyToOne
    @JoinColumn(name = "id_fornecedor")
    private Fornecedor fornecedor;
}
