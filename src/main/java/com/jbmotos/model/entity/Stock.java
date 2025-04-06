package com.jbmotos.model.entity;

import jakarta.persistence.*;

import com.jbmotos.model.enums.StockStatus;

import lombok.*;

@Entity
@Table(name = "stock", schema = "jbmotos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(mappedBy = "stock")
    private Product product;

    private Integer minStock;

    private Integer maxStock;

    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private StockStatus status;
}
