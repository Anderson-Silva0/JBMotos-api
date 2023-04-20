package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.EnderecoDTO;
import com.example.jbmotos.model.entity.Endereco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnderecoServiceImplTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void salvarEndereco() {
    }

    @Test
    void buscarTodosEnderecos() {
    }

    @Test
    void buscarEnderecoPorId() {
    }

    @Test
    void atualizarEndereco() {
    }

    @Test
    void deletarEnderecoPorId() {
    }

    @Test
    void validarEndereco() {
    }

    @Test
    void verificarUsoEndereco() {
    }

    public static Endereco getEndereco() {
        return Endereco.builder()
                .id(1)
                .rua("Rua flores do oriente")
                .cep("51250-545")
                .numero(100)
                .bairro("Jordão Baixo")
                .cidade("Recife")
                .build();
    }

    public static EnderecoDTO getEnderecoDTO() {
        return EnderecoDTO.builder()
                .id(1)
                .rua("Rua flores do oriente")
                .cep("51250-545")
                .numero(100)
                .bairro("Jordão Baixo")
                .cidade("Recife")
                .build();
    }
}