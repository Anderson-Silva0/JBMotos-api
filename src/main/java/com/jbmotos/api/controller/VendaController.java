package com.jbmotos.api.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.jbmotos.api.dto.ProdutoDTO;
import com.jbmotos.api.dto.VendaDTO;
import com.jbmotos.model.entity.Venda;
import com.jbmotos.services.VendaService;

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

    @GetMapping("/filtrar")
    public ResponseEntity<List<VendaDTO>> filtrar(
            @RequestParam(value = "cpfCliente", required = false) String cpfCliente,
            @RequestParam(value = "cpfFuncionario", required = false) String cpfFuncionario
    ) {
        VendaDTO vendaDTO = VendaDTO.builder()
                .cpfCliente(cpfCliente)
                .cpfFuncionario(cpfFuncionario)
                .build();
        return ResponseEntity.ok().body(
                vendaService.filtrarVenda(vendaDTO).stream().map(venda ->
                        mapper.map(venda, VendaDTO.class)
                ).collect(Collectors.toList()));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<VendaDTO> atualizar(@PathVariable("id") Integer id,
                                               @Valid @RequestBody VendaDTO vendaDTO) {
        vendaDTO.setId(id);
        return ResponseEntity.ok().body(mapper.map(vendaService.atualizarVenda(vendaDTO), VendaDTO.class));
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Integer id) {
        vendaService.deletarVenda(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lucro-venda/{idVenda}")
    public ResponseEntity<BigDecimal> lucroDaVenda(@PathVariable("idVenda") Integer idVenda) {
        return ResponseEntity.ok().body(vendaService.calcularLucroDaVenda(idVenda));
    }

    @GetMapping("/valor-total-venda/{id}")
    public ResponseEntity<BigDecimal> valorTotalDaVenda(@PathVariable("id") Integer idVenda) {
        return ResponseEntity.ok().body(vendaService.valorTotalDaVenda(idVenda));
    }

    @GetMapping("/produtos-do-venda/{idVenda}")
    public ResponseEntity<List<ProdutoDTO>> buscarProdutosDaVenda(@PathVariable("idVenda") Integer idVenda) {
        return ResponseEntity.ok().body(
                vendaService.buscarProdutosDaVenda(idVenda).stream().map(produto ->
                        mapper.map(produto, ProdutoDTO.class)
                ).collect(Collectors.toList()));
    }
}
