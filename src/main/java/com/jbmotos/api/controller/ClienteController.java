package com.jbmotos.api.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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

import com.jbmotos.api.dto.ClienteDTO;
import com.jbmotos.model.entity.Cliente;
import com.jbmotos.model.enums.Situacao;
import com.jbmotos.services.ClienteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<ClienteDTO> salvar(@Valid @RequestBody ClienteDTO clienteDTO) {
        Cliente cliente = clienteService.salvarCliente(clienteDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand(cliente).toUri();
        return ResponseEntity.created(uri).body(mapper.map(cliente, ClienteDTO.class));
    }

    @GetMapping("/buscar-todos")
    public ResponseEntity<List<ClienteDTO>> buscarTodos() {
        return ResponseEntity.ok().body(
                clienteService.buscarTodosClientes().stream().map(cliente ->
                        mapper.map(cliente, ClienteDTO.class)
                ).collect(Collectors.toList()));
    }

    @GetMapping("/buscar/{cpf}")
    public ResponseEntity<ClienteDTO> buscarPorCpf(@PathVariable("cpf") String cpf) {
        return ResponseEntity.ok().body(
                mapper.map(clienteService.buscarClientePorCPF(cpf), ClienteDTO.class));
    }

    @GetMapping("/filtrar")
    public ResponseEntity<List<ClienteDTO>> filtrar(
            @RequestParam(value = "cpf", required = false) String cpf,
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "telefone", required = false) String telefone,
            @RequestParam(value = "statusCliente", required = false) String statusCliente
    ) {
        ClienteDTO clienteDTO = ClienteDTO.builder()
                .cpf(cpf)
                .nome(nome)
                .email(email)
                .telefone(telefone)
                .statusCliente(statusCliente)
                .build();
        return ResponseEntity.ok().body(
                clienteService.filtrarCliente(clienteDTO).stream().map(cliente ->
                        mapper.map(cliente, ClienteDTO.class)
                ).collect(Collectors.toList()));
    }

    @PatchMapping("/alternar-status/{cpf}")
    public ResponseEntity<Situacao> alternarStatus(@PathVariable("cpf") String cpf) {
    	Situacao statusCliente = clienteService.alternarStatusCliente(cpf);
        return ResponseEntity.ok().body(statusCliente);
    }

    @PutMapping("/atualizar/{cpf}")
    public ResponseEntity<ClienteDTO> atualizar(@PathVariable("cpf") String cpf,
                                                @Valid @RequestBody ClienteDTO clienteDTO) {
        clienteDTO.setCpf(cpf);
        return ResponseEntity.ok().body(mapper.map(clienteService.atualizarCliente(clienteDTO), ClienteDTO.class));
    }

    @DeleteMapping("/deletar/{cpf}")
    public ResponseEntity<?> deletar(@PathVariable("cpf") String cpf) {
        clienteService.deletarCliente(cpf);
        return ResponseEntity.noContent().build();
    }
}
