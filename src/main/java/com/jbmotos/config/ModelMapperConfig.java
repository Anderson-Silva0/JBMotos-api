package com.jbmotos.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jbmotos.api.dto.ClienteDTO;
import com.jbmotos.api.dto.FornecedorDTO;
import com.jbmotos.api.dto.FuncionarioDTO;
import com.jbmotos.api.dto.ProdutoVendaDTO;
import com.jbmotos.api.dto.VendaDTO;
import com.jbmotos.model.entity.Cliente;
import com.jbmotos.model.entity.Fornecedor;
import com.jbmotos.model.entity.Funcionario;
import com.jbmotos.model.entity.ProdutoVenda;
import com.jbmotos.model.entity.Venda;

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

        return modelMapper;
    }
}
