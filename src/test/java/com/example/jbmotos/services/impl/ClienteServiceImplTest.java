package com.example.jbmotos.services.impl;

import com.example.jbmotos.api.dto.ClienteDTO;
import com.example.jbmotos.model.entity.Cliente;
import com.example.jbmotos.model.entity.Endereco;
import com.example.jbmotos.model.repositories.ClienteRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class ClienteServiceImplTest {

    @Autowired
    private ClienteServiceImpl clienteService;

    @MockBean
    private ClienteRepository clienteRepository;

    @MockBean
    private EnderecoService enderecoService;

    @MockBean
    private ModelMapper mapper;

    private ClienteDTO clienteDTO;
    private Cliente cliente;
    private Endereco endereco;

    @BeforeEach
    public void setUp() {
        clienteDTO = getClienteDTO();
        cliente = getCliente();
        endereco = EnderecoServiceImplTest.getEndereco();
    }

    @Test
    @DisplayName("Deve salvar um cliente com sucesso")
    void salvarCliente() {
        when(clienteRepository.existsClienteByCpf(clienteDTO.getCpf())).thenReturn(false);
        when(clienteRepository.existsClienteByEmail(clienteDTO.getEmail())).thenReturn(false);
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
        assertEquals(cliente.getDataHoraCadastro(), clienteSalvo.getDataHoraCadastro());
        assertNotNull(clienteSalvo.getEndereco());
        assertEquals(endereco, clienteSalvo.getEndereco());

        verify(clienteRepository, times(1)).existsClienteByCpf(anyString());
        verify(clienteRepository, times(1)).existsClienteByEmail(anyString());
        verify(enderecoService, times(1)).buscarEnderecoPorId(anyInt());
        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar salvar cliente com CPF ja cadastrado")
    void erroSalvarClienteComCpfJaCadastrado() {
        when(clienteRepository.existsClienteByCpf(clienteDTO.getCpf())).thenReturn(true);

        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            clienteService.salvarCliente(clienteDTO);
        });
        assertEquals("Erro ao tentar salvar Cliente, CPF já cadastrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar salvar cliente com email ja cadastrado")
    void erroSalvarClienteComEmailJaCadastrado() {
        when(clienteRepository.existsClienteByEmail(clienteDTO.getEmail())).thenReturn(true);

        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            clienteService.salvarCliente(clienteDTO);
        });
        assertEquals("Erro ao tentar salvar Cliente, Email já cadastrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retonar uma lista de clientes com sucesso")
    void buscarTodosClientes() {
        List<Cliente> listaClientes = new ArrayList<>();
        listaClientes.add(cliente);
        listaClientes.add(cliente);
        listaClientes.add(cliente);

        when(clienteRepository.findAll()).thenReturn(listaClientes);

        List<Cliente> clientesRetornados = clienteService.buscarTodosClientes();

        assertNotNull(clientesRetornados);
        assertEquals(3, clientesRetornados.size());
        assertEquals(listaClientes, clientesRetornados);
    }

    @Test
    @DisplayName("Deve buscar um cliente por CPF com sucesso")
    void buscarClientePorCPF() {
        String cpfCliente = "123.456.789-10";

        when(clienteRepository.existsClienteByCpf(cpfCliente)).thenReturn(true);
        when(clienteRepository.findClienteByCpf(cpfCliente)).thenReturn(Optional.of(cliente));

        Optional<Cliente> clienteOptional = clienteService.buscarClientePorCPF(cpfCliente);

        assertNotNull(clienteOptional);
        assertEquals(true, clienteOptional.isPresent());
        assertEquals(cliente, clienteOptional.get());
        assertEquals(Optional.class, clienteOptional.getClass());
    }

    @Test
    @DisplayName("Deve atualizar um cliente com sucesso")
    void atualizarCliente() {
        Cliente clienteAntigo = Cliente.builder()
                .cpf("123.456.789-10")
                .nome("Anderson da Silva")
                .email("anderson@gmail.com")
                .telefone("(81) 91234-5678")
                .dataHoraCadastro(LocalDateTime.now())
                .endereco(endereco)
                .build();
        Cliente novoCliente;

        when(clienteRepository.existsClienteByCpf(clienteDTO.getCpf())).thenReturn(true);
        when(clienteService.buscarClientePorCPF(clienteDTO.getCpf())).thenReturn(Optional.of(clienteAntigo));

        when(mapper.map(clienteDTO, Cliente.class)).thenReturn(
                novoCliente = Cliente.builder()
                        .cpf(clienteDTO.getCpf())
                        .nome(clienteDTO.getNome())
                        .email(clienteDTO.getEmail())
                        .telefone(clienteDTO.getTelefone())
                        .dataHoraCadastro(null)
                        .endereco(null)
                        .build()
        );

        when(enderecoService.buscarEnderecoPorId(clienteDTO.getEndereco())).thenReturn(Optional.of(endereco));
        when(clienteRepository.save(novoCliente)).thenReturn(novoCliente);

        Cliente clienteAtualizado = clienteService.atualizarCliente(clienteDTO);

        assertNotNull(clienteAtualizado);
        assertEquals(clienteDTO.getCpf(), clienteAtualizado.getCpf());
        assertEquals(clienteDTO.getNome(), clienteAtualizado.getNome());
        assertEquals(clienteDTO.getEmail(), clienteAtualizado.getEmail());
        assertEquals(clienteDTO.getTelefone(), clienteAtualizado.getTelefone());
        assertNotNull(clienteAtualizado.getDataHoraCadastro());
        assertEquals(clienteAntigo.getDataHoraCadastro(), clienteAtualizado.getDataHoraCadastro());
        assertNotNull(clienteAtualizado.getEndereco());
        assertEquals(endereco, clienteAtualizado.getEndereco());
        assertEquals(Cliente.class, clienteAtualizado.getClass());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar atualizar um cliente que nao existe")
    void erroAtualizarClienteInexistente() {
        when(clienteRepository.existsClienteByCpf(clienteDTO.getCpf())).thenReturn(false);

        ObjetoNaoEncontradoException exception = assertThrows(ObjetoNaoEncontradoException.class, () -> {
            clienteService.atualizarCliente(clienteDTO);
        });
        assertEquals("Cliente não encrontrado para o CPF informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar atualizar cliente com email ja utilizado por outro cliente")
    void erroAtualizarClienteEmailJaUtilizado() {
        when(clienteRepository.existsClienteByCpf(clienteDTO.getCpf())).thenReturn(true);
        when(clienteService.buscarClientePorCPF(clienteDTO.getCpf())).thenReturn(Optional.of(getCliente()));
        when(clienteRepository.findByCpfNot(clienteDTO.getCpf())).thenReturn(
                Arrays.asList(getCliente(), getCliente())
        );

        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            clienteService.atualizarCliente(clienteDTO);
        });
        assertEquals("Erro ao tentar atualizar Cliente, Email já cadastrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve deletar um cliente com sucesso")
    void deletarCliente() {
        String cpfCliente = "123.456.789-10";

        when(clienteRepository.existsClienteByCpf(cpfCliente)).thenReturn(true);
        doNothing().when(clienteRepository).deleteClienteByCpf(cpfCliente);

        clienteService.deletarCliente(cpfCliente);

        verify(clienteRepository, times(1)).deleteClienteByCpf(cpfCliente);
        verify(clienteRepository, times(1)).existsClienteByCpf(cpfCliente);
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar deletar um cliente que nao existe")
    void erroDeletarClienteInexistente() {
        String cpfCliente = "123.456.789-10";

        when(clienteRepository.existsClienteByCpf(cpfCliente)).thenReturn(false);

        ObjetoNaoEncontradoException exception = assertThrows(ObjetoNaoEncontradoException.class, () -> {
            clienteService.deletarCliente(cpfCliente);
        });
        assertEquals("Cliente não encrontrado para o CPF informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lancar erro ao validar um email ja cadastrado")
    void validarEmailParaSalvar() {
        String email = "anderson@gmail.com";

        when(clienteRepository.existsClienteByEmail(email)).thenReturn(true);

        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            clienteService.validarEmailParaSalvar(email);
        });
        assertEquals("Erro ao tentar salvar Cliente, Email já cadastrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Nao deve lancar erro ao validar um email que ainda nao foi cadastrado")
    void validarEmailParaSalvarSemErro() {
        String email = "anderson@gmail.com";

        when(clienteRepository.existsClienteByEmail(email)).thenReturn(false);

        assertDoesNotThrow(() -> {
            clienteService.validarEmailParaSalvar(email);
        });
    }

    @Test
    @DisplayName("Deve lancar erro ao validar cpf ja cadastrado")
    void validarCpfClienteParaSalvar() {
        String cpf = "123.456.789-10";

        when(clienteRepository.existsClienteByCpf(cpf)).thenReturn(true);

        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            clienteService.validarCpfClienteParaSalvar(cpf);
        });
        assertEquals("Erro ao tentar salvar Cliente, CPF já cadastrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Nao deve lancar erro ao validar cpf que nao foi cadastrado")
    void validarCpfClienteParaSalvarSemErro() {
        String cpf = "123.456.789-10";

        when(clienteRepository.existsClienteByCpf(cpf)).thenReturn(false);

        assertDoesNotThrow(() -> {
            clienteService.validarCpfClienteParaSalvar(cpf);
        });
    }

    @Test
    @DisplayName("Deve lancar erro porque algum outro cliente ja possui email igual")
    void validarEmailParaAtualizar() {
        List<Cliente> listaClientes = new ArrayList<>();
        listaClientes.add(cliente);
        listaClientes.add(cliente);

        when(clienteRepository.findByCpfNot(clienteDTO.getCpf())).thenReturn(listaClientes);

        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            clienteService.validarEmailParaAtualizar(clienteDTO);
        });
        assertEquals("Erro ao tentar atualizar Cliente, Email já cadastrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Nao deve lancar erro porque nenhum cliente possui email igual ao informado")
    void validarEmailParaAtualizarSemErro() {
        List<Cliente> listaClientes = new ArrayList<>();
        Cliente cliente1 = cliente;
        cliente1.setEmail("andre@gmail.com");
        Cliente cliente2 = cliente;
        cliente2.setEmail("adriano@gmail.com");
        listaClientes.add(cliente1);
        listaClientes.add(cliente2);

        when(clienteRepository.findByCpfNot(clienteDTO.getCpf())).thenReturn(listaClientes);

        assertDoesNotThrow(() -> {
            clienteService.validarEmailParaAtualizar(clienteDTO);
        });
    }

    @Test
    @DisplayName("Deve lancar erro porque o cpf nao existe no banco de dados")
    void checarCpfClienteExistente() {
        String cpf = "123.456.789-10";

        when(clienteRepository.existsClienteByCpf(cpf)).thenReturn(false);

        ObjetoNaoEncontradoException exception = assertThrows(ObjetoNaoEncontradoException.class, () -> {
            clienteService.checarCpfClienteExistente(cpf);
        });
        assertEquals("Cliente não encrontrado para o CPF informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Nao deve lancar erro porque o cpf existe no banco de dados")
    void checarCpfClienteExistenteSemErro() {
        String cpf = "123.456.789-10";

        when(clienteRepository.existsClienteByCpf(cpf)).thenReturn(true);

        assertDoesNotThrow(() -> {
            clienteService.checarCpfClienteExistente(cpf);
        });
    }

    @Test
    @DisplayName("Deve retornar true quando existir cliente por id do endereco")
    void existeClientePorIdEndereco() {
        Integer idEndereco = 1;

        when(clienteRepository.existsClienteByEnderecoId(idEndereco)).thenReturn(true);

        assertTrue( clienteService.existeClientePorIdEndereco(idEndereco) );
    }

    @Test
    @DisplayName("Deve retornar false quando não existir cliente por id do endereco")
    void naoExisteClientePorIdEndereco() {
        Integer idEndereco = 1;

        when(clienteRepository.existsClienteByEnderecoId(idEndereco)).thenReturn(false);

        assertFalse( clienteService.existeClientePorIdEndereco(idEndereco) );
    }

    public static Cliente getCliente() {
        return Cliente.builder()
                .cpf("710.606.394-08")
                .nome("Anderson")
                .email("anderson@gmail.com")
                .telefone("(81) 992389161")
                .dataHoraCadastro(null)
                .endereco(null)
                .build();
    }

    public static ClienteDTO getClienteDTO() {
        return ClienteDTO.builder()
                .cpf("710.606.394-08")
                .nome("Anderson")
                .email("anderson@gmail.com")
                .telefone("(81) 992389161")
                .dataHoraCadastro(null)
                .endereco(123)
                .build();
    }
}