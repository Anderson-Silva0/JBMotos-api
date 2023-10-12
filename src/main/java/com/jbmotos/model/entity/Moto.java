package com.jbmotos.model.entity;

import com.jbmotos.model.enums.Situacao;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "moto", schema = "jbmotos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class Moto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 8)
    private String placa;

    @Column(length = 30)
    private String marca;

    @Column(length = 30)
    private String modelo;

    private Integer ano;

    @Enumerated(EnumType.STRING)
    private Situacao statusMoto;

    @CreationTimestamp
    private LocalDateTime dataHoraCadastro;

    @ManyToOne
    @JoinColumn(name = "cpf_cliente", referencedColumnName = "cpf")
    private Cliente cliente;

    @OneToMany(mappedBy = "moto", cascade = CascadeType.ALL)
    private List<Servico> servicos;
 }
