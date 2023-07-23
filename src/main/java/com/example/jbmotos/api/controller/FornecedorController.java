package com.example.jbmotos.api.controller;

import com.example.jbmotos.api.dto.FornecedorDTO;
import com.example.jbmotos.model.entity.Fornecedor;
import com.example.jbmotos.model.enums.StatusFornecedor;
import com.example.jbmotos.model.repositories.FornecedorRepository;
import com.example.jbmotos.services.FornecedorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fornecedor")
public class FornecedorController {

    @Autowired
    private FornecedorService fornecedorService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<FornecedorDTO> salvar(@Valid @RequestBody FornecedorDTO fornecedorDTO) {
        Fornecedor fornecedor = fornecedorService.salvarFornecedor(fornecedorDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand(fornecedor).toUri();
        return ResponseEntity.created(uri).body( mapper.map(fornecedor, FornecedorDTO.class) );
    }

    @GetMapping("/buscar-todos")
    public ResponseEntity<List<FornecedorDTO>> buscarTodos() {
        return ResponseEntity.ok().body(fornecedorService.buscarTodosFornecedores().stream().map(fornecedor ->
                mapper.map(fornecedor, FornecedorDTO.class)
        ).collect(Collectors.toList()));
    }

    @GetMapping("/buscar")
    public ResponseEntity<FornecedorDTO> buscarPorCnpj(@RequestParam("cnpj") String cnpj) {
        return ResponseEntity.ok().body(
                mapper.map(fornecedorService.buscarFornecedorPorCNPJ(cnpj), FornecedorDTO.class));
    }

    @GetMapping("/filtrar")
    public ResponseEntity<List<FornecedorDTO>> filtrar(
            @RequestParam(value = "cnpj", required = false) String cnpj,
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "telefone", required = false) String telefone
    ) {
        FornecedorDTO fornecedorDTO = FornecedorDTO.builder()
                .cnpj(cnpj)
                .nome(nome)
                .telefone(telefone)
                .build();
        return ResponseEntity.ok().body(
                fornecedorService.filtrarFornecedor(fornecedorDTO).stream().map(fornecedor ->
                        mapper.map(fornecedor, FornecedorDTO.class)
                ).collect(Collectors.toList()));
    }

    @PatchMapping("/alternar-status")
    public ResponseEntity<StatusFornecedor> alternarStatus(@RequestParam("cnpj") String cnpj) {
        StatusFornecedor statusFornecedor = fornecedorService.alternarStatusFornecedor(cnpj);
        return ResponseEntity.ok().body(statusFornecedor);
    }

    @PutMapping("/atualizar")
    public ResponseEntity<FornecedorDTO> atualizar(@RequestParam("cnpj") String cnpj,
                                                   @Validated(FornecedorRepository.class) @RequestBody FornecedorDTO fornecedorDTO) {

        fornecedorDTO.setCnpj(cnpj);
        return ResponseEntity.ok().body(
                mapper.map(fornecedorService.atualizarFornecedor(fornecedorDTO), FornecedorDTO.class));
    }

    @DeleteMapping("/deletar")
    public ResponseEntity deletar(@RequestParam("cnpj") String cnpj) {
        fornecedorService.deletarFornecedor(cnpj);
        return ResponseEntity.noContent().build();
    }
}
