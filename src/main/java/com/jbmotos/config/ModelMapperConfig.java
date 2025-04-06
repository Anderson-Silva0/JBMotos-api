package com.jbmotos.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jbmotos.api.dto.CardPaymentDTO;
import com.jbmotos.api.dto.ProductsOfSaleDTO;
import com.jbmotos.model.entity.CardPayment;
import com.jbmotos.model.entity.ProductsOfSale;

@Configuration
public class ModelMapperConfig {

    @Bean
    ModelMapper ModelMapper() {
        var modelMapper = new ModelMapper();

        modelMapper.createTypeMap(ProductsOfSale.class, ProductsOfSaleDTO.class)
                .<Integer>addMapping(src -> src.getSale().getId(), ProductsOfSaleDTO::setSaleId);

        modelMapper.createTypeMap(CardPayment.class, CardPaymentDTO.class)
                .<Integer>addMapping(src -> src.getSale().getId(), CardPaymentDTO::setSaleId)
                .<Integer>addMapping(src -> src.getSale().getId(), CardPaymentDTO::setSaleId);

        return modelMapper;
    }
}
