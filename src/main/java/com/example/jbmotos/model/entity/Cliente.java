package com.example.jbmotos.model.entity;

import com.example.jbmotos.model.enums.StatusCliente;
import lombok.*;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Table(name = "cliente", schema = "jbmotos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Cliente {

    @Id
    @Column(name = "cpf", length = 14)
    private String cpf;

    @Column(name = "nome", length = 50)
    private String nome;

    @Column(name = "email", length = 200)
    private String email;

    @Column(name = "telefone", length = 15)
    private String telefone;

    @Column(name = "statusCliente")
    @Enumerated(EnumType.STRING)
    private StatusCliente statusCliente;

    @Column(name = "data_hora_cadastro")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime dataHoraCadastro;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_endereco", referencedColumnName = "id")
    private Endereco endereco;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Pedido> pedidos;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Moto> motos;
}