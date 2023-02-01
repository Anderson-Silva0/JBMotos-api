package com.example.jbmotos.api.controller;

import com.example.jbmotos.api.dto.EnderecoDTO;
import com.example.jbmotos.services.EnderecoService;
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
@RequestMapping("/api/endereco")
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<EnderecoDTO> salvarEndereco(@Valid @RequestBody EnderecoDTO enderecoDTO) {
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}").buildAndExpand( enderecoService.salvarEndereco(enderecoDTO) ).toUri();
        return ResponseEntity.created(uri).build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<String> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> message = new ArrayList<>();
        ex.getBindingResult().getAllErrors().stream().forEach( erro -> {
            message.add(erro.getDefaultMessage());
        });
        return message;
    }
}
