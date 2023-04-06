package com.example.jbmotos.api.controller;

import com.example.jbmotos.api.dto.ServicoDTO;
import com.example.jbmotos.model.entity.Servico;
import com.example.jbmotos.services.ServicoService;
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
@RequestMapping("/api/servico")
public class ServicoController {

    @Autowired
    private ServicoService servicoService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<ServicoDTO> salvar(@Valid @RequestBody ServicoDTO servicoDTO) {
        Servico servico = servicoService.salvarServico(servicoDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand(servico).toUri();
        return ResponseEntity.created(uri).body(mapper.map(servico, ServicoDTO.class));
    }

    @GetMapping("/buscar-todos")
    public ResponseEntity<List<ServicoDTO>> buscarTodos() {
        return ResponseEntity.ok().body(servicoService.buscarTodosServicos().stream().map(servico ->
                mapper.map(servico, ServicoDTO.class)
        ).collect(Collectors.toList()));
    }

    @GetMapping("/buscar/{idServico}")
    public ResponseEntity<ServicoDTO> buscarPorId(@PathVariable("idServico") Integer idServico) {
        return ResponseEntity.ok().body(mapper.map(servicoService.buscarServicoPorId(idServico), ServicoDTO.class));
    }

    @GetMapping("/buscar-por-pedido/{idPedido}")
    public ResponseEntity<ServicoDTO> buscarPorIdPedido(@PathVariable("idPedido") Integer idPedido) {
        return ResponseEntity.ok(mapper.map(servicoService.buscarServicoPorIdPedido(idPedido), ServicoDTO.class));
    }

    @GetMapping("/buscar-por-cpfFuncionario/{cpfFuncionario}")
    public ResponseEntity<List<ServicoDTO>> buscarPorCpfFuncionario(@PathVariable("cpfFuncionario") String cpf) {
        return ResponseEntity.ok().body(servicoService.buscarServicosPorCpfFuncionario(cpf).stream()
                .map(servico ->
                        mapper.map(servico, ServicoDTO.class)
                ).collect(Collectors.toList()));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<ServicoDTO> atualizar(@PathVariable("id") Integer id,
                                                @Valid @RequestBody ServicoDTO servicoDTO) {
        servicoDTO.setId(id);
        return ResponseEntity.ok().body(mapper.map(servicoService.atualizarServico(servicoDTO), ServicoDTO.class));
    }

    @DeleteMapping("/deletar/{idServico}")
    public ResponseEntity deletar(@PathVariable("idServico") Integer idServico) {
        servicoService.deletarServico(idServico);
        return ResponseEntity.noContent().build();
    }
}
