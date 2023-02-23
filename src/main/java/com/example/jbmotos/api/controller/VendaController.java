package com.example.jbmotos.api.controller;

import com.example.jbmotos.api.dto.VendaDTO;
import com.example.jbmotos.model.entity.Venda;
import com.example.jbmotos.services.VendaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
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
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand( venda ).toUri();
        return ResponseEntity.created(uri).body(mapper.map(venda, VendaDTO.class));
    }

    @GetMapping("/buscar-todas")
    public ResponseEntity<List<VendaDTO>> buscarTodas() {
        return ResponseEntity.ok().body(
                vendaService.buscarTodasVendas().stream().map( venda ->
                        mapper.map(venda, VendaDTO.class)
                ).collect(Collectors.toList()));
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<VendaDTO> buscarPorId(@PathVariable("id") Integer id) {
        return ResponseEntity.ok().body(mapper.map(vendaService.buscarVendaPorId(id), VendaDTO.class));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<VendaDTO> atualizar(@PathVariable("id") Integer id,
                                              @Valid @RequestBody VendaDTO vendaDTO) {
        vendaDTO.setId(id);
        return ResponseEntity.ok().body(mapper.map(vendaService.atualizarVenda(vendaDTO), VendaDTO.class));
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity deletar(@PathVariable("id") Integer id) {
        vendaService.deletarVenda(id);
        return ResponseEntity.noContent().build();
    }
}
