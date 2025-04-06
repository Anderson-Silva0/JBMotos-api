package com.jbmotos.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Entity
@Table(name = "sale", schema = "jbmotos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "customer_cpf")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "employee_cpf")
    private Employee employee;

    @Column(length = 100)
    private String observation;

    @Column(length = 50)
    private String paymentMethod;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "sale", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
    private List<ProductsOfSale> productsOfSale;

    @OneToOne(mappedBy = "sale")
    private Repair repair;

    @OneToOne(mappedBy = "sale", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
    private CardPayment cardPayment;

    private BigDecimal totalSaleValue;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(ZoneId.of("America/Recife"));
    }
}