package com.example.jbmotos.api.controller;

import com.example.jbmotos.api.dto.FornecedorDTO;
import com.example.jbmotos.model.entity.Fornecedor;
import com.example.jbmotos.services.FornecedorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/fornecedor")
public class FornecedorController {

    @Autowired
    private FornecedorService fornecedorService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<FornecedorDTO> salvar(@RequestBody FornecedorDTO fornecedorDTO) {
        Fornecedor fornecedor = fornecedorService.salvarFornecedor(fornecedorDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand(fornecedor).toUri();
        return ResponseEntity.created(uri).body( mapper.map(fornecedor, FornecedorDTO.class) );
    }
}
