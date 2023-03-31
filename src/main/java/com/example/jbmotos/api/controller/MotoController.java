package com.example.jbmotos.api.controller;

import com.example.jbmotos.api.dto.MotoDTO;
import com.example.jbmotos.model.entity.Moto;
import com.example.jbmotos.services.MotoService;
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
@RequestMapping("/api/moto")
public class MotoController {

    @Autowired
    private MotoService motoService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<MotoDTO> salvar(@Valid @RequestBody MotoDTO motoDTO) {
        Moto moto = motoService.salvarMoto(motoDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand(moto).toUri();
        return ResponseEntity.created(uri).body(mapper.map(moto, MotoDTO.class));
    }
}
