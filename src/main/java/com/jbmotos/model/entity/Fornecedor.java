package com.jbmotos.model.entity;

import com.jbmotos.model.enums.Situacao;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Entity
@Table(name = "fornecedor", schema = "jbmotos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class Fornecedor {

    @Id
    @Column(length = 18, unique = true)
    private String cnpj;

    @Column(length = 50)
    private String nome;

    @Column(length = 15)
    private String telefone;

    @Enumerated(EnumType.STRING)
    private Situacao statusFornecedor;

    private LocalDateTime dataHoraCadastro;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_endereco", referencedColumnName = "id")
    private Endereco endereco;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fornecedor")
    private List<Produto> produtos;

    @PrePersist
    public void prePersist() {
        this.dataHoraCadastro = LocalDateTime.now(ZoneId.of("America/Recife"));
    }
}
