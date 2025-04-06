package com.jbmotos.model.entity;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "address", schema = "jbmotos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100)
    private String road;

    @Column(length = 9)
    private String cep;

    private Integer number;

    @Column(length = 50)
    private String neighborhood;

    @Column(length = 50)
    private String city;

    @OneToOne(mappedBy = "address")
    private Customer customer;

    @OneToOne(mappedBy = "address")
    private Employee employee;

    @OneToOne(mappedBy = "address")
    private Supplier supplier;
}
