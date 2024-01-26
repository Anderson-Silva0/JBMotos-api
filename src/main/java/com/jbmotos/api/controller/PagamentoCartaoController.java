package com.jbmotos.api.controller;

import com.jbmotos.api.dto.PagamentoCartaoDTO;
import com.jbmotos.services.PagamentoCartaoService;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pagamentocartao")
public class PagamentoCartaoController {

    @Autowired
    private PagamentoCartaoService service;

    @Autowired
    private ModelMapper mapper;


    @PostMapping
    public ResponseEntity<PagamentoCartaoDTO> salvar(@Valid @RequestBody PagamentoCartaoDTO pagamentoCartaoDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                mapper.map(service.salvarPagamentoCartao(pagamentoCartaoDTO), PagamentoCartaoDTO.class)
        );
    }

    @GetMapping("buscar-todos")
    public ResponseEntity<List<PagamentoCartaoDTO>> buscarTodos() {
        return ResponseEntity.ok().body(
                service.buscarTodosPagamentosCartoes().stream().map(pagamentoCartao ->
                        mapper.map(pagamentoCartao, PagamentoCartaoDTO.class)
                ).collect(Collectors.toList()));
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<PagamentoCartaoDTO> buscarPorId(@PathVariable("id") Integer id) {
        return ResponseEntity.ok().body(
                mapper.map(service.buscarPagamentoCartaoPorId(id), PagamentoCartaoDTO.class)
        );
    }

    @GetMapping("/buscar-por-idVenda/{idVenda}")
    public ResponseEntity<PagamentoCartaoDTO> buscarPorIdVenda(@PathVariable("idVenda") Integer idVenda) {
        return ResponseEntity.ok().body(
               mapper.map(service.buscarPagamentoCartaoPorIdVenda(idVenda), PagamentoCartaoDTO.class)
        );
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<PagamentoCartaoDTO> atualizar(@PathVariable("id") Integer id,
                                                        @Valid @RequestBody PagamentoCartaoDTO pagamentoCartaoDTO) {
        pagamentoCartaoDTO.setId(id);
        return ResponseEntity.ok().body(
                mapper.map(service.atualizarPagamentoCartao(pagamentoCartaoDTO), PagamentoCartaoDTO.class)
        );
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Integer id) {
        service.deletarPagamentoCartao(id);
        return ResponseEntity.noContent().build();
    }
}
