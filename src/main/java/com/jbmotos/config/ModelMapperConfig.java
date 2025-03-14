package com.jbmotos.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jbmotos.api.dto.PagamentoCartaoDTO;
import com.jbmotos.api.dto.ProdutoVendaDTO;
import com.jbmotos.api.dto.VendaDTO;
import com.jbmotos.model.entity.PagamentoCartao;
import com.jbmotos.model.entity.ProdutoVenda;
import com.jbmotos.model.entity.Venda;

@Configuration
public class ModelMapperConfig {

    @Bean
    ModelMapper ModelMapper() {
        var modelMapper = new ModelMapper();

        modelMapper.createTypeMap(ProdutoVenda.class, ProdutoVendaDTO.class)
                .<Integer>addMapping(src -> src.getVenda().getId(), ProdutoVendaDTO::setIdVenda);

        modelMapper.createTypeMap(PagamentoCartao.class, PagamentoCartaoDTO.class)
                .<Integer>addMapping(src -> src.getVenda().getId(), PagamentoCartaoDTO::setIdVenda)
                .<Integer>addMapping(src -> src.getVenda().getId(), PagamentoCartaoDTO::setIdVenda);

        return modelMapper;
    }
}
