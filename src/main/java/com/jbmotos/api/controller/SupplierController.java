package com.jbmotos.api.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.jbmotos.api.dto.SupplierDTO;
import com.jbmotos.model.entity.Supplier;
import com.jbmotos.model.enums.Situation;
import com.jbmotos.services.SupplierService;

@RestController
@RequestMapping("/api/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<SupplierDTO> save(@Valid @RequestBody SupplierDTO supplierDTO) {
        Supplier supplier = this.supplierService.saveSupplier(supplierDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand(supplier).toUri();
        return ResponseEntity.created(uri).body( this.mapper.map(supplier, SupplierDTO.class) );
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<SupplierDTO>> findAll() {
        return ResponseEntity.ok().body(this.supplierService.findAllSuppliers().stream().map(supplier ->
                this.mapper.map(supplier, SupplierDTO.class)
        ).collect(Collectors.toList()));
    }

    @GetMapping("/find")
    public ResponseEntity<SupplierDTO> findByCnpj(@RequestParam("cnpj") String cnpj) {
        return ResponseEntity.ok().body(
                this.mapper.map(this.supplierService.findSupplierByCnpj(cnpj), SupplierDTO.class));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<SupplierDTO>> filter(
            @RequestParam(value = "cnpj", required = false) String cnpj,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "supplierStatus", required = false) String supplierStatus
    ) {
        SupplierDTO supplierDTO = SupplierDTO.builder()
                .cnpj(cnpj)
                .name(name)
                .phone(phone)
                .supplierStatus(supplierStatus)
                .build();
        return ResponseEntity.ok().body(
                this.supplierService.filterSupplier(supplierDTO).stream().map(supplier ->
                        this.mapper.map(supplier, SupplierDTO.class)
                ).collect(Collectors.toList()));
    }

    @PatchMapping("/toggle-status")
    public ResponseEntity<Situation> toggleStatus(@RequestParam("cnpj") String cnpj) {
        Situation supplierStatus = this.supplierService.toggleSupplierStatus(cnpj);
        return ResponseEntity.ok().body(supplierStatus);
    }

    @PutMapping("/update")
    public ResponseEntity<SupplierDTO> update(@RequestParam("cnpj") String cnpj,
                                              @Valid @RequestBody SupplierDTO supplierDTO) {
        supplierDTO.setCnpj(cnpj);
        return ResponseEntity.ok().body(
                this.mapper.map(this.supplierService.updateSupplier(supplierDTO), SupplierDTO.class));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam("cnpj") String cnpj) {
        this.supplierService.deleteSupplier(cnpj);
        return ResponseEntity.noContent().build();
    }
}
