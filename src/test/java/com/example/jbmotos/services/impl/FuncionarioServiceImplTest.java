package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.FuncionarioDTO;
import com.example.jbmotos.model.entity.Endereco;
import com.example.jbmotos.model.entity.Funcionario;
import com.example.jbmotos.model.repositories.FuncionarioRepository;
import com.example.jbmotos.services.EnderecoService;
import com.example.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.example.jbmotos.services.exception.RegraDeNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        endereco = EnderecoServiceImplTest.getEndereco();
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
    @DisplayName("Deve lancar erro ao tentar salvar um funcionario com CPF ja cadastrado")
    void erroSalvarFuncionarioCpfJaCadastrado() {
        when(funcionarioRepository.existsFuncionarioByCpf(funcionarioDTO.getCpf())).thenReturn(true);

        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            funcionarioService.salvarFuncionario(funcionarioDTO);
        });
        assertEquals("Erro ao tentar salvar Funcionário, CPF já cadastrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar uma lista de funcionarios com sucesso")
    void buscarTodosFuncionarios() {
        List<Funcionario> listaFuncionarios = new ArrayList<>();
        listaFuncionarios.add(funcionario);
        listaFuncionarios.add(funcionario);
        listaFuncionarios.add(funcionario);

        when(funcionarioRepository.findAll()).thenReturn(listaFuncionarios);

        List<Funcionario> funcionariosRetornados = funcionarioService.buscarTodosFuncionarios();

        assertNotNull(funcionariosRetornados);
        assertEquals(3, funcionariosRetornados.size());
        assertEquals(listaFuncionarios, funcionariosRetornados);
    }

    @Test
    @DisplayName("Deve buscar um funcionario por CPF com sucesso")
    void buscarFuncionarioPorCPF() {
        String cpfFuncionario = "123.456.789-10";

        when(funcionarioRepository.existsFuncionarioByCpf(cpfFuncionario)).thenReturn(true);
        when(funcionarioRepository.findFuncionarioByCpf(cpfFuncionario)).thenReturn(Optional.of(funcionario));

        Optional<Funcionario> funcionarioOptional = funcionarioService.buscarFuncionarioPorCPF(cpfFuncionario);

        assertNotNull(funcionarioOptional);
        assertEquals(true, funcionarioOptional.isPresent());
        assertEquals(funcionario, funcionarioOptional.get());
        assertEquals(Optional.class, funcionarioOptional.getClass());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar buscar um funcionario inexistente")
    void erroBuscarFuncionarioPorCPF() {
        String cpfFuncionario = "123.456.789-10";

        when(funcionarioRepository.existsFuncionarioByCpf(cpfFuncionario)).thenReturn(false);

        ObjetoNaoEncontradoException exception = assertThrows(ObjetoNaoEncontradoException.class, () -> {
            funcionarioService.buscarFuncionarioPorCPF(cpfFuncionario);
        });
        assertEquals("Funcionário não encrontrado para o CPF informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar um funcionario com sucesso")
    void atualizarFuncionario() {
        Funcionario funcionarioAntigo = Funcionario.builder()
                .cpf("109.876.543-21")
                .nome("Jaelson Batista")
                .telefone("(81) 91111-1111")
                .dataHoraCadastro(LocalDateTime.now())
                .endereco(endereco)
                .build();
        Funcionario novoFuncionario;

        when(funcionarioRepository.existsFuncionarioByCpf(funcionarioDTO.getCpf())).thenReturn(true);
        when(funcionarioService.buscarFuncionarioPorCPF(funcionarioDTO.getCpf()))
                .thenReturn(Optional.of(funcionarioAntigo));

        when(mapper.map(funcionarioDTO, Funcionario.class)).thenReturn(
                novoFuncionario = Funcionario.builder()
                .cpf(funcionarioDTO.getCpf())
                .nome(funcionarioDTO.getNome())
                .telefone(funcionarioDTO.getTelefone())
                .dataHoraCadastro(null)
                .endereco(null)
                .build()
        );

        when(enderecoService.buscarEnderecoPorId(funcionarioDTO.getEndereco())).thenReturn(Optional.of(endereco));
        when(funcionarioRepository.save(novoFuncionario)).thenReturn(novoFuncionario);

        Funcionario funcionarioAtualizado = funcionarioService.atualizarFuncionario(funcionarioDTO);

        assertNotNull(funcionarioAtualizado);
        assertEquals(funcionarioDTO.getCpf(), funcionarioAtualizado.getCpf());
        assertEquals(funcionarioDTO.getNome(), funcionarioAtualizado.getNome());
        assertEquals(funcionarioDTO.getTelefone(), funcionarioAtualizado.getTelefone());
        assertNotNull(funcionarioAtualizado.getDataHoraCadastro());
        assertEquals(funcionarioAntigo.getDataHoraCadastro(), funcionarioAtualizado.getDataHoraCadastro());
        assertNotNull(funcionarioAtualizado.getEndereco());
        assertEquals(endereco, funcionarioAtualizado.getEndereco());
        assertEquals(Funcionario.class, funcionarioAtualizado.getClass());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar atualizar um funcionario que nao existe")
    void erroAtualizarFuncionarioInexistente() {
        when(funcionarioRepository.existsFuncionarioByCpf(funcionarioDTO.getCpf())).thenReturn(false);

        ObjetoNaoEncontradoException exception = assertThrows(ObjetoNaoEncontradoException.class, () -> {
            funcionarioService.atualizarFuncionario(funcionarioDTO);
        });
        assertEquals("Funcionário não encrontrado para o CPF informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve deletar um funcionario com sucesso")
    void deletarFuncionario() {
        String cpfFuncionario = "123.456.789-10";

        when(funcionarioRepository.existsFuncionarioByCpf(cpfFuncionario)).thenReturn(true);
        doNothing().when(funcionarioRepository).deleteFuncionarioByCpf(cpfFuncionario);

        funcionarioService.deletarFuncionario(cpfFuncionario);

        verify(funcionarioRepository, times(1)).existsFuncionarioByCpf(cpfFuncionario);
        verify(funcionarioRepository, times(1)).deleteFuncionarioByCpf(cpfFuncionario);
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar deletar um funcionario que nao existe")
    void erroDeletarFuncionarioInexistente() {
        String cpfFuncionario = "123.456.789-10";

        when(funcionarioRepository.existsFuncionarioByCpf(cpfFuncionario)).thenReturn(false);

        ObjetoNaoEncontradoException exception = assertThrows(ObjetoNaoEncontradoException.class, () -> {
            funcionarioService.deletarFuncionario(cpfFuncionario);
        });
        assertEquals("Funcionário não encrontrado para o CPF informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro ao validar um CPF ja cadastrado")
    void validarCpfFuncionarioParaSalvar() {
        String cpfFuncionario = "123.456.789-10";

        when(funcionarioRepository.existsFuncionarioByCpf(cpfFuncionario)).thenReturn(true);

        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            funcionarioService.validarCpfFuncionarioParaSalvar(cpfFuncionario);
        });
        assertEquals("Erro ao tentar salvar Funcionário, CPF já cadastrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Nao deve lancar erro ao validar um CPF que ainda nao foi cadastrado")
    void validarCpfFuncionarioParaSalvarSemErro() {
        String cpfFuncionario = "123.456.789-10";

        when(funcionarioRepository.existsFuncionarioByCpf(cpfFuncionario)).thenReturn(false);

        assertDoesNotThrow(() -> {
            funcionarioService.validarCpfFuncionarioParaSalvar(cpfFuncionario);
        });
    }

    @Test
    @DisplayName("Deve retornar uma lista de todos funcionarios fora o funcionario informado no parametro")
    void filtrarFuncionariosPorCpfDiferente() {
        List<Funcionario> listaFuncionarios = new ArrayList<>();
        listaFuncionarios.add(funcionario);
        listaFuncionarios.add(funcionario);
        listaFuncionarios.add(funcionario);

        when(funcionarioRepository.findByCpfNot(funcionarioDTO.getCpf())).thenReturn(listaFuncionarios);

        List<Funcionario> funcionariosFiltrados =
                funcionarioService.filtrarFuncionariosPorCpfDiferente(funcionarioDTO);

        assertNotNull(funcionariosFiltrados);
        assertEquals(3, funcionariosFiltrados.size());
        assertEquals(ArrayList.class, funcionariosFiltrados.getClass());
    }

    @Test
    @DisplayName("Deve lancar erro porque o funcionario nao existe")
    void checarCpfFuncionarioExistente() {
        String cpfFuncionario = "109.876.543-21";

        when(funcionarioRepository.existsFuncionarioByCpf(cpfFuncionario)).thenReturn(true);

        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            funcionarioService.validarCpfFuncionarioParaSalvar(cpfFuncionario);
        });
        assertEquals("Erro ao tentar salvar Funcionário, CPF já cadastrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Nao deve lancar erro porque o funcionario existe")
    void checarCpfFuncionarioExistenteSemErro() {
        String cpfFuncionario = "109.876.543-21";

        when(funcionarioRepository.existsFuncionarioByCpf(cpfFuncionario)).thenReturn(false);

        assertDoesNotThrow(() -> {
            funcionarioService.validarCpfFuncionarioParaSalvar(cpfFuncionario);
        });
    }

    @Test
    @DisplayName("Deve retornar true quando existir funcionario por id do endereco")
    void existeFuncionarioPorIdEndereco() {
        Integer idEndereco = 1;

        when(funcionarioRepository.existsFuncionarioByEnderecoId(idEndereco)).thenReturn(true);

        assertTrue( funcionarioService.existeFuncionarioPorIdEndereco(idEndereco) );
    }

    @Test
    @DisplayName("Deve retornar false quando nao existir funcionario por id do endereco")
    void naoExisteFuncionarioPorIdEndereco() {
        Integer idEndereco = 1;

        when(funcionarioRepository.existsFuncionarioByEnderecoId(idEndereco)).thenReturn(false);

        assertFalse( funcionarioService.existeFuncionarioPorIdEndereco(idEndereco) );
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