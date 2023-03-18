package com.example.jbmotos.api.controller;

import com.example.jbmotos.api.dto.PedidoDTO;
import com.example.jbmotos.model.entity.Pedido;
import com.example.jbmotos.services.PedidoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedido")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<PedidoDTO> salvar(@Valid @RequestBody PedidoDTO pedidoDTO) {
        Pedido pedido = pedidoService.salvarPedido(pedidoDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand( pedido ).toUri();
        return ResponseEntity.created(uri).body(mapper.map(pedido, PedidoDTO.class));
    }

    @GetMapping("/buscar-todos")
    public ResponseEntity<List<PedidoDTO>> buscarTodos() {
        return ResponseEntity.ok().body(
                pedidoService.buscarTodosPedidos().stream().map( pedido ->
                        mapper.map(pedido, PedidoDTO.class)
                ).collect(Collectors.toList()));
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<PedidoDTO> buscarPorId(@PathVariable("id") Integer id) {
        return ResponseEntity.ok().body(mapper.map(pedidoService.buscarPedidoPorId(id).get(), PedidoDTO.class));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<PedidoDTO> atualizar(@PathVariable("id") Integer id,
                                              @Valid @RequestBody PedidoDTO pedidoDTO) {
        pedidoDTO.setId(id);
        return ResponseEntity.ok().body(mapper.map(pedidoService.atualizarPedido(pedidoDTO), PedidoDTO.class));
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity deletar(@PathVariable("id") Integer id) {
        pedidoService.deletarPedido(id);
        return ResponseEntity.noContent().build();
    }
}
