package com.jbmotos.api.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import com.jbmotos.api.dto.CustomerDTO;
import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.jbmotos.api.dto.MotorcycleDTO;
import com.jbmotos.model.entity.Motorcycle;
import com.jbmotos.model.enums.Situation;
import com.jbmotos.services.MotorcycleService;

@RestController
@RequestMapping("/api/motorcycle")
public class MotorcycleController {

    @Autowired
    private MotorcycleService motorcycleService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<MotorcycleDTO> save(@Valid @RequestBody MotorcycleDTO motorcycleDTO) {
        Motorcycle motorcycle = this.motorcycleService.saveMotorcycle(motorcycleDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand(motorcycle).toUri();
        return ResponseEntity.created(uri).body(this.mapper.map(motorcycle, MotorcycleDTO.class));
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<MotorcycleDTO>> findAll() {
        return ResponseEntity.ok().body(
                this.motorcycleService.findAllMotorcycles().stream().map(moto ->
                        this.mapper.map(moto, MotorcycleDTO.class)
                ).collect(Collectors.toList()));
    }

    @GetMapping("/find-by-cpf/{customerCpf}")
    public ResponseEntity<List<MotorcycleDTO>> findMotorcyclesByCustomerCpf(@PathVariable("customerCpf") String customerCpf) {
        return ResponseEntity.ok().body(
                this.motorcycleService.findMotorcycleByCustomerCpf(customerCpf).stream().map(motorcycle ->
                        this.mapper.map(motorcycle, MotorcycleDTO.class)
                ).collect(Collectors.toList()));
    }

    @GetMapping("/find-by-id/{motorcycleId}")
    public ResponseEntity<MotorcycleDTO> findById(@PathVariable("motorcycleId") Integer motorcycleId) {
        return ResponseEntity.ok().body(
                this.mapper.map(this.motorcycleService.findMotorcycleById(motorcycleId), MotorcycleDTO.class)
        );
    }

    @GetMapping("/find-by-plate/{plate}")
    public ResponseEntity<MotorcycleDTO> findByPlate(@PathVariable("plate") String plate) {
        return ResponseEntity.ok().body(
                this.mapper.map(this.motorcycleService.findMotorcycleByPlate(plate), MotorcycleDTO.class)
        );
    }

    @GetMapping("/filter")
    public ResponseEntity<List<MotorcycleDTO>> filter(
            @RequestParam(value = "plate", required = false) String plate,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "model", required = false) String model,
            @RequestParam(value = "customerCpf", required = false) String customerCpf,
            @RequestParam(value = "motorcycleStatus", required = false) String motorcycleStatus
    ) {
        CustomerDTO customerDTO = CustomerDTO.builder().cpf(customerCpf).build();

        MotorcycleDTO motorcycleDTO = MotorcycleDTO.builder()
                .plate(plate)
                .brand(brand)
                .model(model)
                .customer(customerDTO)
                .motorcycleStatus(motorcycleStatus)
                .build();
        return ResponseEntity.ok().body(
                this.motorcycleService.filterMotorcycle(motorcycleDTO).stream().map(motorcycle ->
                        this.mapper.map(motorcycle, MotorcycleDTO.class)
                ).collect(Collectors.toList()));
    }

    @PatchMapping("/toggle-status/{id}")
    public ResponseEntity<Situation> toggleStatus(@PathVariable("id") Integer id) {
    	Situation statusMoto = this.motorcycleService.toggleMotorcycleStatus(id);
        return ResponseEntity.ok().body(statusMoto);
    }

    @PutMapping("/update/{motorcycleId}")
    public ResponseEntity<MotorcycleDTO> update(@PathVariable("motorcycleId") Integer motorcycleId,
                                                @Valid @RequestBody MotorcycleDTO motorcycleDTO) {
        motorcycleDTO.setId(motorcycleId);
        return ResponseEntity.ok().body(
                this.mapper.map(this.motorcycleService.updateMotorcycle(motorcycleDTO), MotorcycleDTO.class)
        );
    }

    @DeleteMapping("/delete-by-id/{motorcycleId}")
    public ResponseEntity<?> deleteById(@PathVariable("motorcycleId") Integer motorcycleId) {
        this.motorcycleService.deleteMotorcycleById(motorcycleId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-by-plate/{plate}")
    public ResponseEntity<?> deleteByPlate(@PathVariable("plate") String plate) {
        this.motorcycleService.deleteMotorcycleByPlate(plate);
        return ResponseEntity.noContent().build();
    }
}
