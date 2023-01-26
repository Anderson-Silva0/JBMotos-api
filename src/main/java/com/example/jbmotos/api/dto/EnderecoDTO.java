package com.example.jbmotos.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnderecoDTO {

    private Integer id;

    @NotBlank(message = "O campo Rua é obrigatório")
    @Length(max = 100)
    private String rua;

    @NotBlank(message = "O campo CEP é obrigatório")
    @Length(max = 10)
    private String cep;

    @NotBlank(message = "O campo Numero é obrigatório")
    @Length(max = 5)
    private String numero;

    @NotBlank(message = "O campo Cidade é obrigatório")
    @Length(max = 50)
    private String cidade;
}
