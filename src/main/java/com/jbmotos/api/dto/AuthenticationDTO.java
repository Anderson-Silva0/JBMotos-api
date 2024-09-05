package com.jbmotos.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticationDTO(
        @NotBlank(message = "O login não pode estar vazio")
        @Size(min = 4, message = "O login deve ter pelo menos 4 caracteres")
        String login,

        @NotBlank(message = "A senha não pode estar vazia")
        @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
        String senha
) {
}
