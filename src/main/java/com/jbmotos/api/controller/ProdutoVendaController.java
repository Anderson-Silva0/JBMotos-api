package com.jbmotos.api.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

import com.jbmotos.api.dto.ProdutoVendaDTO;
import com.jbmotos.model.entity.ProdutoVenda;
import com.jbmotos.services.ProdutoVendaService;

@RestController
@RequestMapping("/api/produtovenda")
@Validated
public class ProdutoVendaController {

    @Autowired
    private ProdutoVendaService produtoVendaService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<ProdutoVendaDTO> salvar(@Valid @RequestBody ProdutoVendaDTO produtoVendaDTO){
        ProdutoVenda produtoVenda = produtoVendaService.salvarProdutoVenda(produtoVendaDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand(produtoVenda).toUri();
        return ResponseEntity.created(uri).body(mapper.map(produtoVenda, ProdutoVendaDTO.class));
    }

    @GetMapping("/buscar-todos")
    public ResponseEntity<List<ProdutoVendaDTO>> buscarTodos(){
        return ResponseEntity.ok().body(
                produtoVendaService.buscarTodosProdutoVenda().stream().map(produtoVenda ->
                        mapper.map(produtoVenda, ProdutoVendaDTO.class)
                ).collect(Collectors.toList()));
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<ProdutoVendaDTO> buscarPorId(@PathVariable("id") Integer id){
        return ResponseEntity.ok().body(mapper.map(produtoVendaService.
                buscarProdutoVendaPorId(id), ProdutoVendaDTO.class));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<ProdutoVendaDTO> atualizar(@PathVariable("id") Integer id,
                                                      @RequestParam Integer idVenda,
                                                                    Integer idProduto,
        @Positive(message = "A quantidade deve ser maior que zero") Integer quantidade) {
        ProdutoVendaDTO produtoVendaDTO = ProdutoVendaDTO.builder()
                .id(id)
                .idVenda(idVenda)
                .idProduto(idProduto)
                .quantidade(quantidade)
                .build();
        return ResponseEntity.ok().body(
                mapper.map(produtoVendaService.atualizarProdutoVenda(produtoVendaDTO), ProdutoVendaDTO.class)
        );
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Integer id){
        produtoVendaService.deletarProdutoVendaPorId(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/produtos-venda/{idVenda}")
    public ResponseEntity<List<ProdutoVendaDTO>> buscarTodosPorIdVenda(@PathVariable("idVenda") Integer idVenda){
        return ResponseEntity.ok().body(
                produtoVendaService.buscarProdutoVendaPorIdVenda(idVenda).stream().map(venda ->
                        mapper.map(venda, ProdutoVendaDTO.class)
                ).collect(Collectors.toList()));
    }
}
