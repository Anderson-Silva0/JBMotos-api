package com.example.jbmotos.api.controller;

import com.example.jbmotos.api.dto.ProdutoPedidoDTO;
import com.example.jbmotos.model.entity.ProdutoPedido;
import com.example.jbmotos.services.ProdutoPedidoService;
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
@RequestMapping("/api/produtopedido")
public class ProdutoPedidoController {

    @Autowired
    private ProdutoPedidoService produtoPedidoService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<ProdutoPedidoDTO> salvar(@Valid @RequestBody ProdutoPedidoDTO produtoPedidoDTO) {
        ProdutoPedido produtoPedido = produtoPedidoService.salvarProdutoPedido(produtoPedidoDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand( produtoPedido ).toUri();
        return ResponseEntity.created(uri).body(mapper.map(produtoPedido, ProdutoPedidoDTO.class));
    }

    @GetMapping("/buscar-todos")
    public ResponseEntity<List<ProdutoPedidoDTO>> buscarTodos() {
        return ResponseEntity.ok().body(
                produtoPedidoService.buscarTodosProdutoPedido().stream().map(produtoPedido ->
                        mapper.map(produtoPedido, ProdutoPedidoDTO.class)
                ).collect(Collectors.toList()));
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<ProdutoPedidoDTO> buscarPorId(@PathVariable("id") Integer id) {
        return ResponseEntity.ok().body(mapper.map(produtoPedidoService.
                buscarProdutoPedidoPorId(id), ProdutoPedidoDTO.class));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<ProdutoPedidoDTO> atualizar(@PathVariable("id") Integer id,
                                                      @Valid @RequestBody ProdutoPedidoDTO produtoPedidoDTO) {
        produtoPedidoDTO.setId(id);
        return ResponseEntity.ok().body(mapper.map(produtoPedidoService
                .atualizarProdutoPedido(produtoPedidoDTO), ProdutoPedidoDTO.class));
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity deletar(@PathVariable("id") Integer id) {
        produtoPedidoService.deletarProdutoPedidoPorId(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar-todos-por-pedido/{id}")
    public ResponseEntity<List<ProdutoPedidoDTO>> buscarTodosPorPedido(@PathVariable("id") Integer idPedido) {
        return ResponseEntity.ok().body(
                produtoPedidoService.buscarProdutosDoPedido(idPedido).stream().map(pedido ->
                        mapper.map(pedido, ProdutoPedidoDTO.class)
                ).collect(Collectors.toList()));
    }

    @GetMapping("/valor-total-pedido/{id}")
    public ResponseEntity<BigDecimal> valorTotalDoPedido(@PathVariable("id") Integer idPedido) {
        return ResponseEntity.ok().body( produtoPedidoService.valorTotalDoPedido(idPedido) );
    }
}
