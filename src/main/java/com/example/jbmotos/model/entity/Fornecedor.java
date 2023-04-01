package com.example.jbmotos.model.entity;

import lombok.*;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Table(name = "fornecedor", schema = "jbmotos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Fornecedor {

    @Id
    @Column(name = "cnpj", length = 18)
    private String cnpj;

    @Column(name = "nome", length = 50)
    private String nome;

    @Column(name = "telefone", length = 15)
    private String telefone;

    @Column(name = "data_hora_cadastro")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime dataHoraCadastro;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_endereco", referencedColumnName = "id")
    private Endereco endereco;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fornecedor")
    private List<Produto> produtos;
}
