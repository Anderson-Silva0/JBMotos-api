package com.jbmotos.api.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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

import com.jbmotos.api.dto.MotoDTO;
import com.jbmotos.api.dto.ServicoDTO;
import com.jbmotos.model.entity.Servico;
import com.jbmotos.services.ServicoService;

import jakarta.validation.Valid;

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
    
    @GetMapping("/filtrar")
    public ResponseEntity<List<ServicoDTO>> filtrar(
            @RequestParam(value = "cpfCliente", required = false) String cpfCliente,
            @RequestParam(value = "cpfFuncionario", required = false) String cpfFuncionario,
            @RequestParam(value = "placa", required = false) String placa,
            @RequestParam(value = "servicosRealizados", required = false) String servicosRealizados
    ) {
        ServicoDTO servicoDTO = ServicoDTO.builder()
        		.moto(MotoDTO.builder().cpfCliente(cpfCliente).placa(placa).build())
        		.servicosRealizados(servicosRealizados)
                .cpfFuncionario(cpfFuncionario)
                .build();
        return ResponseEntity.ok().body(
                servicoService.filtrarServico(servicoDTO).stream().map(servico ->
                        mapper.map(servico, ServicoDTO.class)
                ).collect(Collectors.toList()));
    }

    @GetMapping("/buscar-por-venda/{idVenda}")
    public ResponseEntity<ServicoDTO> buscarPorIdVenda(@PathVariable("idVenda") Integer idVenda) {
        return ResponseEntity.ok(mapper.map(servicoService.buscarServicoPorIdVenda(idVenda), ServicoDTO.class));
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
    public ResponseEntity<?> deletar(@PathVariable("idServico") Integer idServico) {
        servicoService.deletarServico(idServico);
        return ResponseEntity.noContent().build();
    }
}
