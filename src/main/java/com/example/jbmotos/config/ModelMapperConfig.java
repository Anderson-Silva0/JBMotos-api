package com.example.jbmotos.config;

import com.example.jbmotos.api.dto.ClienteDTO;
import com.example.jbmotos.api.dto.FuncionarioDTO;
import com.example.jbmotos.model.entity.Cliente;
import com.example.jbmotos.model.entity.Funcionario;
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

        return modelMapper;
    }
}
