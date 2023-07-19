package com.example.jbmotos.api.controller;

import com.example.jbmotos.api.dto.FuncionarioDTO;
import com.example.jbmotos.model.entity.Funcionario;
import com.example.jbmotos.services.FuncionarioService;
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
@RequestMapping("/api/funcionario")
public class FuncionarioController {

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<FuncionarioDTO> salvar(@Valid @RequestBody FuncionarioDTO funcionarioDTO) {
        Funcionario funcionario = funcionarioService.salvarFuncionario(funcionarioDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand( funcionario ).toUri();
        return ResponseEntity.created(uri).body( mapper.map(funcionario, FuncionarioDTO.class) );
    }

    @GetMapping("/buscar-todos")
    public ResponseEntity<List<FuncionarioDTO>> buscarTodos() {
        return ResponseEntity.ok().body(
                funcionarioService.buscarTodosFuncionarios().stream().map(funcionario ->
                        mapper.map(funcionario, FuncionarioDTO.class)
                        ).collect(Collectors.toList()));
    }

    @GetMapping("/buscar/{cpf}")
    public ResponseEntity<FuncionarioDTO> buscarPorCpf(@PathVariable("cpf") String cpf) {
        return ResponseEntity.ok().body(
                mapper.map(funcionarioService.buscarFuncionarioPorCPF(cpf), FuncionarioDTO.class));
    }

    @GetMapping("/filtrar")
    public ResponseEntity<List<FuncionarioDTO>> filtrar(
            @RequestParam(value = "cpf", required = false) String cpf,
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "telefone", required = false) String telefone
    ) {
        FuncionarioDTO funcionarioDTO = FuncionarioDTO.builder()
                .cpf(cpf)
                .nome(nome)
                .telefone(telefone)
                .build();
        return ResponseEntity.ok().body(
                funcionarioService.filtrarFuncionario(funcionarioDTO).stream().map(funcionario ->
                        mapper.map(funcionario, FuncionarioDTO.class)
                ).collect(Collectors.toList()));
    }

    @PutMapping("/atualizar/{cpf}")
    public ResponseEntity<FuncionarioDTO> atualizar(@PathVariable("cpf") String cpf,
                                                   @Valid @RequestBody FuncionarioDTO funcionarioDTO) {
        funcionarioDTO.setCpf(cpf);
        return ResponseEntity.ok().body(
                mapper.map(funcionarioService.atualizarFuncionario(funcionarioDTO), FuncionarioDTO.class));
    }

    @DeleteMapping("/deletar/{cpf}")
    public ResponseEntity deletar(@PathVariable("cpf") String cpf) {
        funcionarioService.deletarFuncionario(cpf);
        return ResponseEntity.noContent().build();
    }
}
