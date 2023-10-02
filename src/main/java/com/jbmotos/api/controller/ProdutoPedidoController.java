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

import com.jbmotos.api.dto.ProdutoPedidoDTO;
import com.jbmotos.model.entity.ProdutoPedido;
import com.jbmotos.services.ProdutoPedidoService;

@RestController
@RequestMapping("/api/produtopedido")
@Validated
public class ProdutoPedidoController {

    @Autowired
    private ProdutoPedidoService produtoPedidoService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<ProdutoPedidoDTO> salvar(@Valid @RequestBody ProdutoPedidoDTO produtoPedidoDTO){
        ProdutoPedido produtoPedido = produtoPedidoService.salvarProdutoPedido(produtoPedidoDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand(produtoPedido).toUri();
        return ResponseEntity.created(uri).body(mapper.map(produtoPedido, ProdutoPedidoDTO.class));
    }

    @GetMapping("/buscar-todos")
    public ResponseEntity<List<ProdutoPedidoDTO>> buscarTodos(){
        return ResponseEntity.ok().body(
                produtoPedidoService.buscarTodosProdutoPedido().stream().map(produtoPedido ->
                        mapper.map(produtoPedido, ProdutoPedidoDTO.class)
                ).collect(Collectors.toList()));
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<ProdutoPedidoDTO> buscarPorId(@PathVariable("id") Integer id){
        return ResponseEntity.ok().body(mapper.map(produtoPedidoService.
                buscarProdutoPedidoPorId(id), ProdutoPedidoDTO.class));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<ProdutoPedidoDTO> atualizar(@PathVariable("id") Integer id,
                                                      @RequestParam Integer idPedido,
                                                                    Integer idProduto,
        @Positive(message = "A quantidade deve ser maior que zero") Integer quantidade) {
        ProdutoPedidoDTO produtoPedidoDTO = ProdutoPedidoDTO.builder()
                .id(id)
                .idPedido(idPedido)
                .idProduto(idProduto)
                .quantidade(quantidade)
                .build();
        return ResponseEntity.ok().body(
                mapper.map(produtoPedidoService.atualizarProdutoPedido(produtoPedidoDTO), ProdutoPedidoDTO.class)
        );
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Integer id){
        produtoPedidoService.deletarProdutoPedidoPorId(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/produtos-pedido/{idPedido}")
    public ResponseEntity<List<ProdutoPedidoDTO>> buscarTodosPorIdPedido(@PathVariable("idPedido") Integer idPedido){
        return ResponseEntity.ok().body(
                produtoPedidoService.buscarProdutoPedidoPorIdPedido(idPedido).stream().map(pedido ->
                        mapper.map(pedido, ProdutoPedidoDTO.class)
                ).collect(Collectors.toList()));
    }
}
