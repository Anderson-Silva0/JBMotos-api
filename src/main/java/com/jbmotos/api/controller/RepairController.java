package com.jbmotos.api.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import com.jbmotos.api.dto.CustomerDTO;
import com.jbmotos.api.dto.EmployeeDTO;
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

import com.jbmotos.api.dto.MotorcycleDTO;
import com.jbmotos.api.dto.RepairDTO;
import com.jbmotos.model.entity.Repair;
import com.jbmotos.services.RepairService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/repair")
public class RepairController {

    @Autowired
    private RepairService repairService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<RepairDTO> save(@Valid @RequestBody RepairDTO repairDTO) {
        Repair repair = this.repairService.saveRepair(repairDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand(repair).toUri();
        return ResponseEntity.created(uri).body(this.mapper.map(repair, RepairDTO.class));
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<RepairDTO>> findAll() {
        return ResponseEntity.ok().body(this.repairService.findAllRepairs().stream().map(servico ->
                this.mapper.map(servico, RepairDTO.class)
        ).collect(Collectors.toList()));
    }

    @GetMapping("/find/{repairId}")
    public ResponseEntity<RepairDTO> findById(@PathVariable("repairId") Integer repairId) {
        return ResponseEntity.ok().body(this.mapper.map(this.repairService.findRepairById(repairId), RepairDTO.class));
    }
    
    @GetMapping("/filter")
    public ResponseEntity<List<RepairDTO>> filter(
            @RequestParam(value = "customerCpf", required = false) String customerCpf,
            @RequestParam(value = "employeeCpf", required = false) String employeeCpf,
            @RequestParam(value = "plate", required = false) String plate,
            @RequestParam(value = "repairsPerformed", required = false) String repairsPerformed
    ) {
        CustomerDTO customerDTO = CustomerDTO.builder().cpf(customerCpf).build();
        EmployeeDTO employeeDTO = EmployeeDTO.builder().cpf(employeeCpf).build();

        RepairDTO repairDTO = RepairDTO.builder()
        		.motorcycle(MotorcycleDTO.builder().customer(customerDTO).plate(plate).build())
        		.repairsPerformed(repairsPerformed)
                .employee(employeeDTO)
                .build();
        return ResponseEntity.ok().body(
                this.repairService.filterRepair(repairDTO).stream().map(repair ->
                        this.mapper.map(repair, RepairDTO.class)
                ).collect(Collectors.toList()));
    }

    @GetMapping("/find-by-sale-id/{saleId}")
    public ResponseEntity<RepairDTO> findBySaleId(@PathVariable("saleId") Integer saleId) {
        return ResponseEntity.ok(this.mapper.map(this.repairService.findRepairBySaleId(saleId), RepairDTO.class));
    }

    @GetMapping("/find-by-employeeCpf/{employeeCpf}")
    public ResponseEntity<List<RepairDTO>> findByEmployeeCpf(@PathVariable("employeeCpf") String employeeCpf) {
        return ResponseEntity.ok().body(this.repairService.findRepairByEmployeeCpf(employeeCpf).stream()
                .map(repair -> this.mapper.map(repair, RepairDTO.class))
                .collect(Collectors.toList()));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<RepairDTO> update(@PathVariable("id") Integer id,
                                            @Valid @RequestBody RepairDTO repairDTO) {
        repairDTO.setId(id);
        return ResponseEntity.ok().body(this.mapper.map(this.repairService.updateRepair(repairDTO), RepairDTO.class));
    }

    @DeleteMapping("/delete/{repairId}")
    public ResponseEntity<?> delete(@PathVariable("repairId") Integer repairId) {
        this.repairService.deleteRepair(repairId);
        return ResponseEntity.noContent().build();
    }
}
