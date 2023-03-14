package com.example.jbmotos.api.controller;

import com.example.jbmotos.api.dto.ProdutoPedidoDTO;
import com.example.jbmotos.model.entity.ProdutoPedido;
import com.example.jbmotos.services.ProdutoPedidoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/produto-pedido")
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

}
