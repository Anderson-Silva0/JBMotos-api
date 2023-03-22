package com.example.jbmotos.api.controller;

import com.example.jbmotos.api.dto.EstoqueDTO;
import com.example.jbmotos.model.entity.Estoque;
import com.example.jbmotos.services.EstoqueService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    @Autowired
    private EstoqueService estoqueService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<EstoqueDTO> salvar(@Valid @RequestBody EstoqueDTO estoqueDTO) {
        Estoque estoque = estoqueService.salvarEstoque(estoqueDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand(estoque).toUri();
        return ResponseEntity.created(uri).body(mapper.map(estoque, EstoqueDTO.class));
    }

}
