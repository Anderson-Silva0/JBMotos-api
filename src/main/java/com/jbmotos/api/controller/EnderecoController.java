package com.jbmotos.api.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.jbmotos.api.dto.EnderecoDTO;
import com.jbmotos.model.entity.Endereco;
import com.jbmotos.services.EnderecoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/endereco")
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<EnderecoDTO> salvar(@Valid @RequestBody EnderecoDTO enderecoDTO) {
        Endereco endereco = enderecoService.salvarEndereco(enderecoDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand( endereco ).toUri();
        return ResponseEntity.created(uri).body(mapper.map(endereco, EnderecoDTO.class));
    }
    
    @GetMapping("/buscar-todos")
    public ResponseEntity<List<EnderecoDTO>> buscarTodos() {
        return ResponseEntity.ok().body(
                enderecoService.buscarTodosEnderecos().stream().map(endereco ->
                        mapper.map(endereco, EnderecoDTO.class)
                ).collect(Collectors.toList()));
    }
    
    @GetMapping("/buscar/{id}")
    public ResponseEntity<EnderecoDTO> buscarPorId(@PathVariable("id") Integer id) {
        return ResponseEntity.ok().body(mapper.map(enderecoService.buscarEnderecoPorId(id), EnderecoDTO.class));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<EnderecoDTO> atualizar(@PathVariable("id") Integer id,
                                                 @Valid @RequestBody EnderecoDTO enderecoDTO) {
        enderecoDTO.setId(id);
        return ResponseEntity.ok().body(mapper.map(enderecoService.atualizarEndereco(enderecoDTO), EnderecoDTO.class));
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<?> deletarPorId(@PathVariable("id") Integer id) {
        enderecoService.deletarEnderecoPorId(id);
        return ResponseEntity.noContent().build();
    }
}
