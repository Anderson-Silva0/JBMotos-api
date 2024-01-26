package com.jbmotos.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.jbmotos.api.dto.FornecedorDTO;
import com.jbmotos.model.entity.Endereco;
import com.jbmotos.model.entity.Fornecedor;
import com.jbmotos.model.repositories.FornecedorRepository;
import com.jbmotos.services.EnderecoService;

@SpringBootTest
class FornecedorServiceImplTest {

    @Autowired
    private FornecedorServiceImpl fornecedorService;

    @MockBean
    private FornecedorRepository fornecedorRepository;

    @MockBean
    private EnderecoService enderecoService;

    @MockBean
    private ModelMapper mapper;

    private Fornecedor fornecedor;
    private FornecedorDTO fornecedorDTO;
    private Endereco endereco;

    @BeforeEach
    void setUp() {
        this.fornecedor = getFornecedor();
        this.fornecedorDTO = getFornecedorDTO();
        this.endereco = EnderecoServiceImplTest.getEndereco();
    }

    @Test
    @DisplayName("Deve salvar um Fornecedor com sucesso")
    void salvarFornecedor() {
        // Cenário
        when(fornecedorRepository.existsFornecedorByCnpj(fornecedorDTO.getCnpj())).thenReturn(false);
        when(mapper.map(fornecedorDTO, Fornecedor.class)).thenReturn(fornecedor);
        when(enderecoService.buscarEnderecoPorId(fornecedorDTO.getEndereco())).thenReturn(endereco);
        when(fornecedorRepository.save(any(Fornecedor.class))).thenReturn(fornecedor);

        // Execução
        Fornecedor fornecedorSalvo = fornecedorService.salvarFornecedor(fornecedorDTO);

        //Verificação
        assertNotNull(fornecedorSalvo);
        assertNotNull(fornecedorSalvo.getEndereco());
        assertEquals(fornecedor.getCnpj(), fornecedorSalvo.getCnpj());
        assertEquals(fornecedor.getNome(), fornecedorSalvo.getNome());
        assertEquals(fornecedor.getTelefone(), fornecedorSalvo.getTelefone());
        verify(fornecedorRepository, times(1)).save(any(Fornecedor.class));
    }

    @Test
    void buscarTodosFornecedores() {
    }

    @Test
    void buscarFornecedorPorCNPJ() {
    }

    @Test
    void atualizarFornecedor() {
    }

    @Test
    void deletarFornecedor() {
    }

    @Test
    void validarCnpjFornecedorParaSalvar() {
    }

    @Test
    void filtrarFornecedoresPorCnpjDiferente() {
    }

    @Test
    void checarCnpjFornecedorExistente() {
    }

    @Test
    void existeFornecedorPorIdEndereco() {
    }

    public static Fornecedor getFornecedor() {
        return Fornecedor.builder()
                .cnpj("21.300.144/0001-33")
                .nome("Maringá")
                .telefone("(81) 98311-0568")
                .dataHoraCadastro(null)
                .endereco(null)
                .build();
    }

    public static FornecedorDTO getFornecedorDTO() {
        return FornecedorDTO.builder()
                .cnpj("21.300.144/0001-33")
                .nome("Maringá")
                .telefone("(81) 98311-0568")
                .dataHoraCadastro(null)
                .endereco(null)
                .build();
    }
}