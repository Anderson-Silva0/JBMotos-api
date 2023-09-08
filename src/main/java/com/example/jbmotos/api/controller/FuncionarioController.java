package com.example.jbmotos.api.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.jbmotos.api.dto.FuncionarioDTO;
import com.example.jbmotos.model.entity.Funcionario;
import com.example.jbmotos.model.enums.Situacao;
import com.example.jbmotos.services.FuncionarioService;

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
            @RequestParam(value = "telefone", required = false) String telefone,
            @RequestParam(value = "statusFuncionario", required = false) String statusFuncionario
    ) {
        FuncionarioDTO funcionarioDTO = FuncionarioDTO.builder()
                .cpf(cpf)
                .nome(nome)
                .telefone(telefone)
                .statusFuncionario(statusFuncionario)
                .build();
        return ResponseEntity.ok().body(
                funcionarioService.filtrarFuncionario(funcionarioDTO).stream().map(funcionario ->
                        mapper.map(funcionario, FuncionarioDTO.class)
                ).collect(Collectors.toList()));
    }

    @PatchMapping("/alternar-status/{cpf}")
    public ResponseEntity<Situacao> alternarStatus(@PathVariable("cpf") String cpf) {
    	Situacao statusFuncionario = funcionarioService.alternarStatusFuncionario(cpf);
        return ResponseEntity.ok().body(statusFuncionario);
    }

    @PutMapping("/atualizar/{cpf}")
    public ResponseEntity<FuncionarioDTO> atualizar(@PathVariable("cpf") String cpf,
                                                   @Valid @RequestBody FuncionarioDTO funcionarioDTO) {
        funcionarioDTO.setCpf(cpf);
        return ResponseEntity.ok().body(
                mapper.map(funcionarioService.atualizarFuncionario(funcionarioDTO), FuncionarioDTO.class));
    }

    @DeleteMapping("/deletar/{cpf}")
    public ResponseEntity<?> deletar(@PathVariable("cpf") String cpf) {
        funcionarioService.deletarFuncionario(cpf);
        return ResponseEntity.noContent().build();
    }
}
