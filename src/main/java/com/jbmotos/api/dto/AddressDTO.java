package com.jbmotos.api.dto;

import jakarta.validation.constraints.*;

import lombok.*;

import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class AddressDTO {

    private Integer id;

    @NotBlank(message = "O campo Rua é obrigatório.")
    @Length(min = 3, max = 100, message = "O campo Rua deve ter entre 3 e 100 caracteres.")
    private String road;

    @NotBlank(message = "O campo CEP é obrigatório.")
    @Length(min = 9, max = 9, message = "O campo CEP deve ter 9 caracteres.")
    @Pattern(regexp = "^\\d{5}-\\d{3}$", message = "Formato de CEP inválido.")
    private String cep;

    @NotNull(message = "O campo Numero é obrigatório.")
    private Integer number;

    @NotBlank(message = "O campo Bairro é obrigatório.")
    @Length(min =  3, max = 50, message = "O campo Bairro deve ter entre 3 e 50 caracteres.")
    private String neighborhood;

    @NotBlank(message = "O campo Cidade é obrigatório.")
    @Length(min =  3, max = 50, message = "O campo Cidade deve ter entre 3 e 50 caracteres.")
    private String city;
}
