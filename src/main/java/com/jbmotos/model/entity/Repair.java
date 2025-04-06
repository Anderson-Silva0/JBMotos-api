package com.jbmotos.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "repair", schema = "jbmotos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class Repair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "employee_cpf", referencedColumnName = "cpf")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "motorcycle_id", referencedColumnName = "id")
    private Motorcycle motorcycle;

    @OneToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
    @JoinColumn(name = "sale_id", referencedColumnName = "id")
    private Sale sale;

    private LocalDateTime createdAt;

    @Column(length = 300)
    private String repairsPerformed;

    @Column(length = 300)
    private String observation;

    private BigDecimal laborCost;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(ZoneId.of("America/Recife"));
    }
}
