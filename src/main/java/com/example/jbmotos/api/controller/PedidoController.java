package com.example.jbmotos.api.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.jbmotos.api.dto.PedidoDTO;
import com.example.jbmotos.api.dto.ProdutoDTO;
import com.example.jbmotos.model.entity.Pedido;
import com.example.jbmotos.services.PedidoService;

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

    @GetMapping("/filtrar")
    public ResponseEntity<List<PedidoDTO>> filtrar(
            @RequestParam(value = "cpfCliente", required = false) String cpfCliente,
            @RequestParam(value = "cpfFuncionario", required = false) String cpfFuncionario
    ) {
        PedidoDTO pedidoDTO = PedidoDTO.builder()
                .cpfCliente(cpfCliente)
                .cpfFuncionario(cpfFuncionario)
                .build();
        return ResponseEntity.ok().body(
                pedidoService.filtrarPedido(pedidoDTO).stream().map(pedido ->
                        mapper.map(pedido, PedidoDTO.class)
                ).collect(Collectors.toList()));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<PedidoDTO> atualizar(@PathVariable("id") Integer id,
                                               @Valid @RequestBody PedidoDTO pedidoDTO) {
        pedidoDTO.setId(id);
        return ResponseEntity.ok().body(mapper.map(pedidoService.atualizarPedido(pedidoDTO), PedidoDTO.class));
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Integer id) {
        pedidoService.deletarPedido(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lucro-pedido/{idPedido}")
    public ResponseEntity<BigDecimal> lucroDoPedido(@PathVariable("idPedido") Integer idPedido) {
        return ResponseEntity.ok().body(pedidoService.calcularLucroDoPedido(idPedido));
    }

    @GetMapping("/valor-total-pedido/{id}")
    public ResponseEntity<BigDecimal> valorTotalDoPedido(@PathVariable("id") Integer idPedido) {
        return ResponseEntity.ok().body(pedidoService.valorTotalDoPedido(idPedido));
    }

    @GetMapping("/produtos-do-pedido/{idPedido}")
    public ResponseEntity<List<ProdutoDTO>> buscarProdutosDoPedido(@PathVariable("idPedido") Integer idPedido) {
        return ResponseEntity.ok().body(
                pedidoService.buscarProdutosDoPedido(idPedido).stream().map(produto ->
                        mapper.map(produto, ProdutoDTO.class)
                ).collect(Collectors.toList()));
    }
}
