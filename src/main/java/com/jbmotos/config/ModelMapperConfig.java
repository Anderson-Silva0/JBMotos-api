package com.jbmotos.config;

import com.jbmotos.api.dto.*;
import com.jbmotos.model.entity.*;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    ModelMapper ModelMapper() {
        var modelMapper = new ModelMapper();

        modelMapper.createTypeMap(Cliente.class, ClienteDTO.class)
                .<Integer>addMapping(src -> src.getEndereco().getId(), ClienteDTO::setEndereco);

        modelMapper.createTypeMap(Funcionario.class, FuncionarioDTO.class)
                .<Integer>addMapping(src -> src.getEndereco().getId(), FuncionarioDTO::setEndereco);

        modelMapper.createTypeMap(Fornecedor.class, FornecedorDTO.class)
                        .<Integer>addMapping(src -> src.getEndereco().getId(), FornecedorDTO::setEndereco);

        modelMapper.createTypeMap(Venda.class, VendaDTO.class)
                .<String>addMapping(src -> src.getCliente().getCpf(), VendaDTO::setCpfCliente)
                .<String>addMapping(src -> src.getFuncionario().getCpf(), VendaDTO::setCpfFuncionario);

        modelMapper.createTypeMap(ProdutoVenda.class, ProdutoVendaDTO.class)
                .<Integer>addMapping(src -> src.getProduto().getId(), ProdutoVendaDTO::setIdProduto)
                .<Integer>addMapping(src -> src.getVenda().getId(), ProdutoVendaDTO::setIdVenda);

        modelMapper.createTypeMap(PagamentoCartao.class, PagamentoCartaoDTO.class)
                .<Integer>addMapping(src -> src.getVenda().getId(), PagamentoCartaoDTO::setIdVenda)
                .<Integer>addMapping(src -> src.getVenda().getId(), PagamentoCartaoDTO::setIdVenda);

        return modelMapper;
    }
}
