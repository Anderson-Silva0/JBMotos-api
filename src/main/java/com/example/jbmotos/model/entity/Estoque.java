package com.example.jbmotos.model.entity;

import com.example.jbmotos.model.enums.StatusEstoque;
import lombok.*;

import javax.persistence.*;

@Table(name = "estoque", schema = "jbmotos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(mappedBy = "estoque")
    private Produto produto;

    @Column(name = "estoque_minimo")
    private Integer estoqueMinimo;

    @Column(name = "estoque_maximo")
    private Integer estoqueMaximo;

    @Column(name="quantidade")
    private Integer quantidade;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusEstoque status;
}
