package com.example.jbmotos.api.controller;

import com.example.jbmotos.api.dto.ClienteDTO;
import com.example.jbmotos.model.entity.Cliente;
import com.example.jbmotos.services.ClienteService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<ClienteDTO> salvarCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
       Cliente cliente = clienteService.salvarCliente(clienteDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand(cliente).toUri();
        return ResponseEntity.created(uri).build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<String> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> mensagens = new ArrayList<>();
        ex.getBindingResult().getAllErrors().stream().forEach( erro -> {
            mensagens.add(erro.getDefaultMessage());
        });
        return mensagens;
    }
}
