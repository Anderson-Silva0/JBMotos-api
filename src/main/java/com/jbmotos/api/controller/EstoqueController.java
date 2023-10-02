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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.jbmotos.api.dto.EstoqueDTO;
import com.jbmotos.model.entity.Estoque;
import com.jbmotos.services.EstoqueService;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    @Autowired
    private EstoqueService estoqueService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<EstoqueDTO> salvar(@Valid @RequestBody EstoqueDTO estoqueDTO){
        Estoque estoque = estoqueService.salvarEstoque(estoqueDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand(estoque).toUri();
        return ResponseEntity.created(uri).body(mapper.map(estoque, EstoqueDTO.class));
    }

    @GetMapping("/buscar-todos")
    public ResponseEntity<List<EstoqueDTO>> buscarTodos(){
        return ResponseEntity.ok().body(
                estoqueService.buscarTodosEstoques().stream().map(estoque ->
                        mapper.map(estoque, EstoqueDTO.class)
                ).collect(Collectors.toList()));
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<EstoqueDTO> buscarPorId(@PathVariable("id") Integer id) {
        return ResponseEntity.ok().body(mapper.map(estoqueService.buscarEstoquePorId(id), EstoqueDTO.class));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<EstoqueDTO> atualizar(@PathVariable("id") Integer id,
                                                @Valid @RequestBody EstoqueDTO estoqueDTO) {
        estoqueDTO.setId(id);
        return ResponseEntity.ok().body(mapper.map(estoqueService.atualizarEstoque(estoqueDTO), EstoqueDTO.class));
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Integer id) {
        estoqueService.deletarEstoquePorId(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{idProduto}/adicionar")
    public ResponseEntity<String> adicionarQuantidade(@PathVariable("idProduto") Integer idProduto,
                                                      @RequestParam Integer quantidade) {
        estoqueService.adicionarQuantidadeAoEstoque(idProduto, quantidade);
        Integer qtdProdutoEstoque = estoqueService.obterQuantidadeDoProduto(idProduto);
        return ResponseEntity.ok().body("A quantidade de "+quantidade+" foi adicionada ao estoque. O estoque atual " +
                "é de "+qtdProdutoEstoque);
    }

    @GetMapping("/valorTotal")
    public ResponseEntity<BigDecimal> valorTotalEstoque() {
        return ResponseEntity.ok().body(estoqueService.calcularValorTotalEstoque());
    }
}
