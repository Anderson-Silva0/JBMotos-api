package com.example.jbmotos.services.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.example.jbmotos.api.dto.EnderecoDTO;
import com.example.jbmotos.model.entity.Endereco;
import com.example.jbmotos.model.repositories.EnderecoRepository;
import com.example.jbmotos.services.ClienteService;
import com.example.jbmotos.services.FornecedorService;
import com.example.jbmotos.services.FuncionarioService;
import com.example.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.example.jbmotos.services.exception.RegraDeNegocioException;

@SpringBootTest
@ActiveProfiles("test")
class EnderecoServiceImplTest {

    @Autowired
    private EnderecoServiceImpl enderecoService;

    @MockBean
    private EnderecoRepository enderecoRepository;

    @MockBean
    private ClienteService clienteService;

    @MockBean
    private FuncionarioService funcionarioService;

    @MockBean
    private FornecedorService fornecedorService;

    @MockBean
    private ModelMapper mapper;

    private Endereco endereco;
    private EnderecoDTO enderecoDTO;

    @BeforeEach
    void setUp() {
        this.endereco = getEndereco();
        this.enderecoDTO = getEnderecoDTO();
    }

    @Test
    @DisplayName("Deve salvar um Endereco sucesso")
    void salvarEndereco() {
        //Cenário
        when(mapper.map(any(), any())).thenReturn(endereco);
        when(enderecoRepository.save(endereco)).thenReturn(endereco);

        //Execução
        Endereco enderecoSalvo = enderecoService.salvarEndereco(enderecoDTO);

        //Verificação
        assertNotNull(enderecoSalvo);
        assertEquals(endereco.getId(), enderecoSalvo.getId());
        assertEquals(endereco.getRua(), enderecoSalvo.getRua());
        assertEquals(endereco.getCep(), enderecoSalvo.getCep());
        assertEquals(endereco.getNumero(), enderecoSalvo.getNumero());
        assertEquals(endereco.getBairro(), enderecoSalvo.getBairro());
        assertEquals(endereco.getCidade(), enderecoSalvo.getCidade());
    }

    @Test
    @DisplayName("Deve retornar uma lista de Enderecos")
    void buscarTodosEnderecos() {
        //Cenário
        List<Endereco> listaEnderecos = new ArrayList<>();
        listaEnderecos.add(endereco);
        listaEnderecos.add(endereco);
        listaEnderecos.add(endereco);

        when(enderecoRepository.findAll()).thenReturn(listaEnderecos);

        //Execução
        List<Endereco> enderecosRetornados = enderecoService.buscarTodosEnderecos();

        //Verificação
        assertNotNull(enderecosRetornados);
        assertEquals(3, enderecosRetornados.size());
        assertEquals(listaEnderecos, enderecosRetornados);
    }

    @Test
    @DisplayName("Deve buscar um Endereco por id com sucesso")
    void buscarEnderecoPorId() {
        //Cenário
        when(enderecoRepository.existsById(endereco.getId())).thenReturn(true);
        when(enderecoRepository.findById(endereco.getId())).thenReturn(Optional.of(endereco));

        //Execução
        Optional<Endereco> enderecoOptional = enderecoService.buscarEnderecoPorId(endereco.getId());

        //Verificação
        assertNotNull(enderecoOptional);
        assertEquals(true, enderecoOptional.isPresent());
        assertEquals(endereco, enderecoOptional.get());
        assertEquals(Optional.class, enderecoOptional.getClass());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar buscar um Endereco por id")
    void erroBuscarEnderecoPorId() {
        //Cenário
        when(enderecoRepository.existsById(endereco.getId())).thenReturn(false);

        //Execução e verificação
        ObjetoNaoEncontradoException exception = assertThrows(ObjetoNaoEncontradoException.class, () -> {
            enderecoService.buscarEnderecoPorId(endereco.getId());
        });
        assertEquals("Endereço não encontrado para o Id informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar um Endereco com sucesso")
    void atualizarEndereco() {
        //Cenário
        when(enderecoRepository.existsById(endereco.getId())).thenReturn(true);
        when(mapper.map(any(), any())).thenReturn(endereco);
        when(enderecoRepository.save(any())).thenReturn(endereco);

        //Execução
        Endereco enderecoAtualizado = enderecoService.atualizarEndereco(enderecoDTO);

        //Verificação
        assertNotNull(enderecoAtualizado);
        assertEquals(endereco.getId(), enderecoAtualizado.getId());
        verify(enderecoRepository, times(1)).existsById(endereco.getId());
        verify(mapper, times(1)).map(any(), any());
        verify(enderecoRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar atualizar um Endereco")
    void erroAtualizarEndereco() {
        //Cenário
        when(enderecoRepository.existsById(endereco.getId())).thenReturn(false);

        //Execução e verificação
        ObjetoNaoEncontradoException exception = assertThrows(ObjetoNaoEncontradoException.class, () -> {
            enderecoService.atualizarEndereco(enderecoDTO);
        });
        assertEquals("Endereço não encontrado para o Id informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve deletar um Endereco com sucesso")
    void deletarEnderecoPorId() {
        //Cenário
        when(enderecoRepository.existsById(endereco.getId())).thenReturn(true);
        doNothing().when(enderecoRepository).deleteById(endereco.getId());

        //Execução
        enderecoService.deletarEnderecoPorId(endereco.getId());

        //Verificação
        verify(enderecoRepository , times(1)).deleteById(endereco.getId());
        verify(enderecoRepository , times(1)).existsById(endereco.getId());
        verify(clienteService , times(1)).existeClientePorIdEndereco(endereco.getId());
        verify(funcionarioService , times(1)).existeFuncionarioPorIdEndereco(endereco.getId());
        verify(fornecedorService , times(1)).existeFornecedorPorIdEndereco(endereco.getId());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar deletar um Endereco que nao existe")
    void erroDeletarEnderecoInexistente() {
        //Cenário
        when(enderecoRepository.existsById(endereco.getId())).thenReturn(false);

        //Execução e verificação
        ObjetoNaoEncontradoException exception = assertThrows(ObjetoNaoEncontradoException.class, () -> {
            enderecoService.deletarEnderecoPorId(endereco.getId());
        });
        assertEquals("Endereço não encontrado para o Id informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar deletar um Endereco que está sendo usado por um Cliente")
    void erroDeletarEnderecoEmUsoPorUmCliente() {
        //Cenário
        when(enderecoRepository.existsById(endereco.getId())).thenReturn(true);
        when(clienteService.existeClientePorIdEndereco(endereco.getId())).thenReturn(true);

        //Execução e verificação
        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            enderecoService.deletarEnderecoPorId(endereco.getId());
        });
        assertEquals("Erro ao tentar deletar, o Endereço pertence a um Cliente.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar deletar um Endereco que está sendo usado por um Funcionario")
    void erroDeletarEnderecoEmUsoPorUmFuncionario() {
        //Cenário
        when(enderecoRepository.existsById(endereco.getId())).thenReturn(true);
        when(funcionarioService.existeFuncionarioPorIdEndereco(endereco.getId())).thenReturn(true);

        //Execução e verificação
        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            enderecoService.deletarEnderecoPorId(endereco.getId());
        });
        assertEquals("Erro ao tentar deletar, o Endereço pertence a um Funcionário.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar deletar um Endereco que está sendo usado por um Fornecedor")
    void erroDeletarEnderecoEmUsoPorUmFornecedor() {
        //Cenário
        when(enderecoRepository.existsById(endereco.getId())).thenReturn(true);
        when(fornecedorService.existeFornecedorPorIdEndereco(endereco.getId())).thenReturn(true);

        //Execução e verificação
        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            enderecoService.deletarEnderecoPorId(endereco.getId());
        });
        assertEquals("Erro ao tentar deletar, o Endereço pertence a um Fornecedor.", exception.getMessage());
    }

    @Test
    void validarEnderecoSemErro() {
        //Cenário
        when(enderecoRepository.existsById(anyInt())).thenReturn(true);

        //Execução
        assertDoesNotThrow(() -> enderecoService.validarEndereco(1));

        //Verificação
        verify(enderecoRepository).existsById(1);
    }

    @Test
    void validarEnderecoComErro() {
        //Cenário
        when(enderecoRepository.existsById(anyInt())).thenReturn(false);

        //Execução e verificação
        ObjetoNaoEncontradoException exception = assertThrows(ObjetoNaoEncontradoException.class, () -> {
            enderecoService.validarEndereco(1);
        });
        assertEquals("Endereço não encontrado para o Id informado.", exception.getMessage());
        verify(enderecoRepository).existsById(1);
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