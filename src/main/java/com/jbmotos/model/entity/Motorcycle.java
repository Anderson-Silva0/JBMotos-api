package com.jbmotos.model.entity;

import com.jbmotos.model.enums.Situation;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Entity
@Table(name = "motorcycle", schema = "jbmotos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class Motorcycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 8)
    private String plate;

    @Column(length = 30)
    private String brand;

    @Column(length = 30)
    private String model;

    private Integer year;

    @Enumerated(EnumType.STRING)
    private Situation motorcycleStatus;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "customer_cpf", referencedColumnName = "cpf")
    private Customer customer;

    @OneToMany(mappedBy = "motorcycle", cascade = CascadeType.ALL)
    private List<Repair> repairs;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(ZoneId.of("America/Recife"));
    }
}
