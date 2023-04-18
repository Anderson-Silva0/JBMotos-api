package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.FuncionarioDTO;
import com.example.jbmotos.model.entity.Endereco;
import com.example.jbmotos.model.entity.Funcionario;
import com.example.jbmotos.model.repositories.FuncionarioRepository;
import com.example.jbmotos.services.EnderecoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static com.example.jbmotos.services.impl.ClienteServiceImplTest.getEndereco;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class FuncionarioServiceImplTest {

    @Autowired
    private FuncionarioServiceImpl funcionarioService;

    @MockBean
    private FuncionarioRepository funcionarioRepository;

    @MockBean
    private EnderecoService enderecoService;

    @MockBean
    private ModelMapper mapper;

    private Funcionario funcionario;
    private FuncionarioDTO funcionarioDTO;
    private Endereco endereco;


    @BeforeEach
    void setUp() {
        funcionario = getFuncionario();
        funcionarioDTO = getFuncionarioDTO();
        endereco = getEndereco();
    }

    @Test
    @DisplayName("Deve salvar um funcionario com sucesso")
    void salvarFuncionario() {
        when(funcionarioRepository.existsFuncionarioByCpf(funcionarioDTO.getCpf())).thenReturn(false);
        when(mapper.map(funcionarioDTO, Funcionario.class)).thenReturn(funcionario);
        when(funcionarioRepository.save(funcionario)).thenReturn(funcionario);
        when(enderecoService.buscarEnderecoPorId(funcionarioDTO.getEndereco())).thenReturn(Optional.of(endereco));

        Funcionario funcionarioSalvo = funcionarioService.salvarFuncionario(funcionarioDTO);

        assertNotNull(funcionarioSalvo);
        assertEquals(funcionarioDTO.getCpf(), funcionarioSalvo.getCpf());
        assertEquals(funcionarioDTO.getNome(), funcionarioSalvo.getNome());
        assertEquals(funcionarioDTO.getTelefone(), funcionarioSalvo.getTelefone());
        assertNotNull(funcionarioSalvo.getDataHoraCadastro());
        assertEquals(funcionario.getDataHoraCadastro(), funcionarioSalvo.getDataHoraCadastro());
        assertNotNull(funcionarioSalvo.getEndereco());
        assertEquals(funcionario.getEndereco(), funcionarioSalvo.getEndereco());

        verify(funcionarioRepository, times(1)).existsFuncionarioByCpf(funcionarioDTO.getCpf());
        verify(enderecoService, times(1)).buscarEnderecoPorId(funcionarioDTO.getEndereco());
        verify(funcionarioRepository, times(1)).save(funcionario);
    }

    @Test
    void buscarTodosFuncionarios() {
    }

    @Test
    void buscarFuncionarioPorCPF() {
    }

    @Test
    void atualizarFuncionario() {
    }

    @Test
    void deletarFuncionario() {
    }

    @Test
    void validarCpfFuncionarioParaSalvar() {
    }

    @Test
    void filtrarFuncionariosPorCpfDiferente() {
    }

    @Test
    void checarCpfFuncionarioExistente() {
    }

    @Test
    void existeFuncionarioPorIdEndereco() {
    }

    public static Funcionario getFuncionario() {
        return Funcionario.builder()
                .cpf("123.456.789-10")
                .nome("Jabison Batista")
                .telefone("(81) 91234-5678")
                .dataHoraCadastro(null)
                .endereco(null)
                .build();
    }

    public static FuncionarioDTO getFuncionarioDTO() {
        return FuncionarioDTO.builder()
                .cpf("123.456.789-10")
                .nome("Jabison Batista")
                .telefone("(81) 91234-5678")
                .dataHoraCadastro(null)
                .endereco(1)
                .build();
    }
}