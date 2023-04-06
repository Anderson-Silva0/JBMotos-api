package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.ClienteDTO;
import com.example.jbmotos.model.entity.Cliente;
import com.example.jbmotos.model.entity.Endereco;
import com.example.jbmotos.model.repositories.ClienteRepository;
import com.example.jbmotos.services.ClienteService;
import com.example.jbmotos.services.EnderecoService;
import com.example.jbmotos.services.FornecedorService;
import com.example.jbmotos.services.FuncionarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
class ClienteServiceImplTest {

    @Autowired
    private ClienteService clienteService;

    @MockBean
    private ClienteRepository clienteRepository;

    @MockBean
    private FuncionarioService funcionarioService;

    @MockBean
    private FornecedorService fornecedorService;

    @MockBean
    private EnderecoService enderecoService;

    @MockBean
    private ModelMapper mapper;


    @Test
    @DisplayName("Deve salvar um cliente com sucesso")
    void salvarCliente() {
        ClienteDTO clienteDTO = getClienteDTO();
        Cliente cliente = getCliente();
        Endereco endereco = getEndereco();

        when(clienteRepository.existsClienteByCpf(clienteDTO.getCpf())).thenReturn(false);
        when(clienteRepository.existsClienteByEmail(clienteDTO.getEmail())).thenReturn(false);
        when(clienteService.existeClientePorIdEndereco(anyInt())).thenReturn(false);
        when(funcionarioService.existeFuncionarioPorIdEndereco(anyInt())).thenReturn(false);
        when(fornecedorService.existeFornecedorPorIdEndereco(anyInt())).thenReturn(false);
        when(mapper.map(clienteDTO, Cliente.class)).thenReturn(cliente);
        when(enderecoService.buscarEnderecoPorId(clienteDTO.getEndereco())).thenReturn(Optional.of(endereco));
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        Cliente clienteSalvo = clienteService.salvarCliente(clienteDTO);

        assertNotNull(clienteSalvo);
        assertEquals(clienteDTO.getCpf(), clienteSalvo.getCpf());
        assertEquals(clienteDTO.getNome(), clienteSalvo.getNome());
        assertEquals(clienteDTO.getEmail(), clienteSalvo.getEmail());
        assertEquals(clienteDTO.getTelefone(), clienteSalvo.getTelefone());
        assertNotNull(clienteSalvo.getDataHoraCadastro());
        assertEquals(endereco, clienteSalvo.getEndereco());

        verify(clienteRepository, times(1)).existsClienteByCpf(anyString());
        verify(clienteRepository, times(1)).existsClienteByEmail(anyString());
        verify(enderecoService, times(1)).buscarEnderecoPorId(anyInt());
        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    void buscarTodosClientes() {
    }

    @Test
    void buscarClientePorCPF(){
    }

    @Test
    void atualizarCliente(){
    }

    @Test
    void deletarCliente(){
    }

    @Test
    void validarEmailParaSalvar(){
    }

    @Test
    void validarEnderecoParaSalvar(){
    }

    @Test
    void validarCpfClienteParaSalvar(){
    }

    @Test
    void validarEmailParaAtualizar(){
    }

    @Test
    void validarEnderecoParaAtualizar(){
    }

    @Test
    void checarCpfClienteExistente(){
    }

    @Test
    void existeClientePorIdEndereco(){
    }

    public static ClienteDTO getClienteDTO() {
        return ClienteDTO.builder()
                .cpf("710.606.394.08")
                .nome("Anderson")
                .email("anderson@gmail.com")
                .telefone("(81) 992389161")
                .dataHoraCadastro(null)
                .endereco(123)
                .build();
    }

    public static Cliente getCliente() {
        return Cliente.builder()
                .cpf("710.606.394.08")
                .nome("Anderson")
                .email("anderson@gmail.com")
                .telefone("(81) 992389161")
                .dataHoraCadastro(null)
                .endereco(null)
                .build();
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
}