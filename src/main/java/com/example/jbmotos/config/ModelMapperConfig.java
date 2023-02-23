package com.example.jbmotos.config;

import com.example.jbmotos.api.dto.ClienteDTO;
import com.example.jbmotos.api.dto.FuncionarioDTO;
import com.example.jbmotos.api.dto.VendaDTO;
import com.example.jbmotos.model.entity.Cliente;
import com.example.jbmotos.model.entity.Funcionario;
import com.example.jbmotos.model.entity.Venda;
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

        modelMapper.createTypeMap(Venda.class, VendaDTO.class)
                .<String>addMapping(src -> src.getCliente().getCpf(), (dest, valor) -> dest.setCpf_cliente(valor))
                .<String>addMapping(src -> src.getFuncionario().getCpf(), (dest, valor) -> dest.setCpf_funcionario(valor));

        return modelMapper;
    }
}
