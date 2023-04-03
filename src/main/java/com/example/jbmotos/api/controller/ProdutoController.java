package com.example.jbmotos.api.controller;

import com.example.jbmotos.api.dto.ProdutoDTO;
import com.example.jbmotos.model.entity.Produto;
import com.example.jbmotos.services.ProdutoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<ProdutoDTO> atualizar(@PathVariable("id") Integer id,
                                                @Valid @RequestBody ProdutoDTO produtoDTO) {
        produtoDTO.setId(id);
        return ResponseEntity.ok().body(mapper.map(produtoService.atualizarProduto(produtoDTO), ProdutoDTO.class));
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity deletar(@PathVariable("id") Integer id) {
        produtoService.deletarProduto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("lucro-produto/{idProduto}")
    public ResponseEntity<BigDecimal> lucroProduto(@PathVariable("idProduto") Integer idProduto) {
        return ResponseEntity.ok().body(produtoService.calcularLucroProduto(idProduto));
    }
}
