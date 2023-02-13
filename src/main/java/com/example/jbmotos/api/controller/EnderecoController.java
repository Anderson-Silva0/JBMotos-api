package com.example.jbmotos.api.controller;

import com.example.jbmotos.api.dto.EnderecoDTO;
import com.example.jbmotos.model.entity.Endereco;
import com.example.jbmotos.services.EnderecoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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
    @GetMapping("buscar/{id}")
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
    public ResponseEntity deletarPorId(@PathVariable("id") Integer id) {
        enderecoService.deletarEnderecoPorId(id);
        return ResponseEntity.noContent().build();
    }
}
