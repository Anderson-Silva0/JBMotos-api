package com.example.jbmotos.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.jbmotos.api.dto.ClienteDTO;
import com.example.jbmotos.api.dto.FornecedorDTO;
import com.example.jbmotos.api.dto.FuncionarioDTO;
import com.example.jbmotos.api.dto.PedidoDTO;
import com.example.jbmotos.api.dto.ProdutoPedidoDTO;
import com.example.jbmotos.model.entity.Cliente;
import com.example.jbmotos.model.entity.Fornecedor;
import com.example.jbmotos.model.entity.Funcionario;
import com.example.jbmotos.model.entity.Pedido;
import com.example.jbmotos.model.entity.ProdutoPedido;

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

        modelMapper.createTypeMap(Pedido.class, PedidoDTO.class)
                .<String>addMapping(src -> src.getCliente().getCpf(), PedidoDTO::setCpfCliente)
                .<String>addMapping(src -> src.getFuncionario().getCpf(), PedidoDTO::setCpfFuncionario);

        modelMapper.createTypeMap(ProdutoPedido.class, ProdutoPedidoDTO.class)
                .<Integer>addMapping(src -> src.getProduto().getId(), ProdutoPedidoDTO::setIdProduto)
                .<Integer>addMapping(src -> src.getPedido().getId(), ProdutoPedidoDTO::setIdPedido);

        return modelMapper;
    }
}
