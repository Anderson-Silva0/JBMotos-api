package com.example.jbmotos.config;

import com.example.jbmotos.api.dto.ClienteDTO;
import com.example.jbmotos.model.entity.Cliente;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper ModelMapper() {
        var modelMapper = new ModelMapper();

        modelMapper.createTypeMap(Cliente.class, ClienteDTO.class)
                .<Integer>addMapping(src -> src.getEndereco().getId(), (dest, value) -> dest.setEndereco(value));

        return modelMapper;
    }
}
