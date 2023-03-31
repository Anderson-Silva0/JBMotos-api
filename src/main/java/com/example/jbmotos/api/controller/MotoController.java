package com.example.jbmotos.api.controller;

import com.example.jbmotos.api.dto.MotoDTO;
import com.example.jbmotos.model.entity.Moto;
import com.example.jbmotos.services.MotoService;
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
@RequestMapping("/api/moto")
public class MotoController {

    @Autowired
    private MotoService motoService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<MotoDTO> salvar(@Valid @RequestBody MotoDTO motoDTO) {
        Moto moto = motoService.salvarMoto(motoDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand(moto).toUri();
        return ResponseEntity.created(uri).body(mapper.map(moto, MotoDTO.class));
    }

    @GetMapping("/buscar-todas")
    public ResponseEntity<List<MotoDTO>> buscarTodas() {
        return ResponseEntity.ok().body(
                motoService.buscarTodasMotos().stream().map(moto ->
                        mapper.map(moto, MotoDTO.class)
                ).collect(Collectors.toList()));
    }

    @GetMapping("/buscar-por-cpf/{cpfCliente}")
    public ResponseEntity<List<MotoDTO>> buscarMotosPorCpfCliente(@PathVariable("cpfCliente") String cpfCliente) {
        return ResponseEntity.ok().body(
                motoService.buscarMotosPorCpfCliente(cpfCliente).stream().map(moto ->
                        mapper.map(moto, MotoDTO.class)
                ).collect(Collectors.toList()));
    }

    @GetMapping("/buscar-por-id/{idMoto}")
    public ResponseEntity<MotoDTO> buscarPorId(@PathVariable("idMoto") Integer idMoto) {
        return ResponseEntity.ok().body(mapper.map(motoService.buscarMotoPorId(idMoto), MotoDTO.class));
    }

    @GetMapping("/buscar-por-placa/{placa}")
    public ResponseEntity<MotoDTO> buscarPorPlaca(@PathVariable("placa") String placa) {
        return ResponseEntity.ok().body(mapper.map(motoService.buscarMotoPorPlaca(placa), MotoDTO.class));
    }

    @PutMapping("/atualizar/{idMoto}")
    public ResponseEntity<MotoDTO> atualizar(@PathVariable("idMoto") Integer idMoto,
                                             @Valid @RequestBody MotoDTO motoDTO) {
        motoDTO.setId(idMoto);
        return ResponseEntity.ok().body(mapper.map(motoService.atualizarMoto(motoDTO), MotoDTO.class));
    }

    @DeleteMapping("/deletar-por-id/{idMoto}")
    public ResponseEntity deletarPorId(@PathVariable("idMoto") Integer idMoto) {
        motoService.deletarMotoPorId(idMoto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deletar-por-placa/{placa}")
    public ResponseEntity deletarPorPlaca(@PathVariable("placa") String placa) {
        motoService.deletarMotoPorPlaca(placa);
        return ResponseEntity.noContent().build();
    }
}
