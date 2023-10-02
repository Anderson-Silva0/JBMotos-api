package com.jbmotos.api.controller;

import java.math.BigDecimal;
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

import com.jbmotos.api.dto.ProdutoDTO;
import com.jbmotos.model.entity.Produto;
import com.jbmotos.model.enums.Situacao;
import com.jbmotos.services.ProdutoService;

@RestController
@RequestMapping("/api/produto")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<ProdutoDTO> salvar(@Valid @RequestBody ProdutoDTO produtoDTO) {
        Produto produto = produtoService.salvarProduto(produtoDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand( produto ).toUri();
        return ResponseEntity.created(uri).body(mapper.map(produto, ProdutoDTO.class));
    }

    @GetMapping("/buscar-todos")
    public ResponseEntity<List<ProdutoDTO>> buscarTodos() {
        return ResponseEntity.ok().body(
                produtoService.buscarTodosProdutos().stream().map(produto ->
                        mapper.map(produto, ProdutoDTO.class)
                        ).collect(Collectors.toList()));
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<ProdutoDTO> buscarPorId(@PathVariable("id") Integer id) {
        return ResponseEntity.ok().body(mapper.map(produtoService.buscarProdutoPorId(id), ProdutoDTO.class));
    }

    @GetMapping("/filtrar")
    public ResponseEntity<List<ProdutoDTO>> filtrar(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "marca", required = false) String marca,
            @RequestParam(value = "statusProduto", required = false) String statusProduto
    ) {
        ProdutoDTO produtoDTO = ProdutoDTO.builder()
                .nome(nome)
                .marca(marca)
                .statusProduto(statusProduto)
                .build();
        return ResponseEntity.ok().body(
                produtoService.filtrarProduto(produtoDTO).stream().map(produto ->
                        mapper.map(produto, ProdutoDTO.class)
                ).collect(Collectors.toList()));
    }

    @PatchMapping("/alternar-status/{id}")
    public ResponseEntity<Situacao> alternarStatus(@PathVariable("id") Integer id) {
    	Situacao statusProduto = produtoService.alternarStatusProduto(id);
        return ResponseEntity.ok().body(statusProduto);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<ProdutoDTO> atualizar(@PathVariable("id") Integer id,
                                                @Valid @RequestBody ProdutoDTO produtoDTO) {
        produtoDTO.setId(id);
        return ResponseEntity.ok().body(mapper.map(produtoService.atualizarProduto(produtoDTO), ProdutoDTO.class));
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Integer id) {
        produtoService.deletarProduto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("lucro-produto/{idProduto}")
    public ResponseEntity<BigDecimal> lucroProduto(@PathVariable("idProduto") Integer idProduto) {
        return ResponseEntity.ok().body(produtoService.calcularLucroProduto(idProduto));
    }
}
