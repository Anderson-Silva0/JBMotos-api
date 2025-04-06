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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.jbmotos.api.dto.AddressDTO;
import com.jbmotos.model.entity.Address;
import com.jbmotos.services.AddressService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<AddressDTO> save(@Valid @RequestBody AddressDTO addressDTO) {
        Address address = this.addressService.saveAddress(addressDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand(address).toUri();
        return ResponseEntity.created(uri).body(this.mapper.map(address, AddressDTO.class));
    }
    
    @GetMapping("/find-all")
    public ResponseEntity<List<AddressDTO>> findAll() {
        return ResponseEntity.ok().body(
                this.addressService.findAllAddress().stream().map(address ->
                        this.mapper.map(address, AddressDTO.class)
                ).collect(Collectors.toList()));
    }
    
    @GetMapping("/find/{id}")
    public ResponseEntity<AddressDTO> findById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok().body(
                this.mapper.map(this.addressService.findAddressById(id), AddressDTO.class)
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AddressDTO> update(@PathVariable("id") Integer id,
                                             @Valid @RequestBody AddressDTO addressDTO) {
        addressDTO.setId(id);
        return ResponseEntity.ok().body(
                this.mapper.map(this.addressService.updateAddress(addressDTO), AddressDTO.class)
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") Integer id) {
        this.addressService.deleteAddressById(id);
        return ResponseEntity.noContent().build();
    }
}
