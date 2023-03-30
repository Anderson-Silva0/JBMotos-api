package com.example.jbmotos.config;

import com.example.jbmotos.api.dto.*;
import com.example.jbmotos.model.entity.*;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper ModelMapper() {
        var modelMapper = new ModelMapper();

        modelMapper.createTypeMap(Cliente.class, ClienteDTO.class)
                .<Integer>addMapping(src -> src.getEndereco().getId(), (dest, valor) -> dest.setEndereco(valor));

        modelMapper.createTypeMap(Funcionario.class, FuncionarioDTO.class)
                .<Integer>addMapping(src -> src.getEndereco().getId(), (dest, valor) -> dest.setEndereco(valor));

        modelMapper.createTypeMap(Fornecedor.class, FornecedorDTO.class)
                        .<Integer>addMapping(src -> src.getEndereco().getId(), (dest, valor) -> dest.setEndereco(valor));

        modelMapper.createTypeMap(Pedido.class, PedidoDTO.class)
                .<String>addMapping(src -> src.getCliente().getCpf(), (dest, valor) -> dest.setCpfCliente(valor))
                .<String>addMapping(src -> src.getFuncionario().getCpf(), (dest, valor) -> dest.setCpfFuncionario(valor));

        modelMapper.createTypeMap(ProdutoPedido.class, ProdutoPedidoDTO.class)
                .<Integer>addMapping(src -> src.getProduto().getId(), (dest, valor) -> dest.setIdProduto(valor))
                .<Integer>addMapping(src -> src.getPedido().getId(), (dest, valor) -> dest.setIdPedido(valor));

        return modelMapper;
    }
}
