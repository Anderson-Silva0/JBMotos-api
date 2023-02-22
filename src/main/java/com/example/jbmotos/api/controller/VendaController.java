package com.example.jbmotos.api.controller;

import com.example.jbmotos.api.dto.VendaDTO;
import com.example.jbmotos.model.entity.Venda;
import com.example.jbmotos.services.VendaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/venda")
public class VendaController {

    @Autowired
    private VendaService vendaService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<VendaDTO> salvar(@Valid @RequestBody VendaDTO vendaDTO) {
        Venda venda = vendaService.salvarVenda(vendaDTO);
        return ResponseEntity.ok().body(mapper.map(venda, VendaDTO.class));
    }

    public ResponseEntity<List<VendaDTO>> buscarTodas() {
        return ResponseEntity.ok().body(
                vendaService.buscarTodasVendas().stream().map( venda ->
                        mapper.map(venda, VendaDTO.class)
                ).collect(Collectors.toList()));
    }
}
