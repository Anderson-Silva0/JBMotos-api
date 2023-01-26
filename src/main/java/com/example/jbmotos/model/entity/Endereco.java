package com.example.jbmotos.model.entity;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

@Table(name = "endereco", schema = "jbmotos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_endereco")
    private Integer id;

    @Column(name = "rua")
    @NotBlank(message = "O campo Rua é obrigatório")
    @Length(max = 100)
    private String rua;

    @Column(name = "cep")
    @NotBlank(message = "O campo CEP é obrigatório")
    @Length(max = 10)
    private String cep;

    @Column(name = "numero")
    @NotBlank(message = "O campo Numero é obrigatório")
    @Length(max = 5)
    private String numero;

    @Column(name = "cidade")
    @NotBlank(message = "O campo Cidade é obrigatório")
    @Length(max = 50)
    private String cidade;
}
