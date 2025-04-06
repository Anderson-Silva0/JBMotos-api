package com.jbmotos.api.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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

import com.jbmotos.api.dto.CustomerDTO;
import com.jbmotos.model.entity.Customer;
import com.jbmotos.model.enums.Situation;
import com.jbmotos.services.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<CustomerDTO> save(@Valid @RequestBody CustomerDTO customerDTO) {
        Customer customer = this.customerService.saveCustomer(customerDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand(customer).toUri();
        return ResponseEntity.created(uri).body(this.mapper.map(customer, CustomerDTO.class));
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<CustomerDTO>> findAll() {
        return ResponseEntity.ok().body(
                this.customerService.findAllCustomer().stream().map(cliente ->
                        this.mapper.map(cliente, CustomerDTO.class)
                ).collect(Collectors.toList()));
    }

    @GetMapping("/find/{cpf}")
    public ResponseEntity<CustomerDTO> findByCpf(@PathVariable("cpf") String cpf) {
        return ResponseEntity.ok().body(
                this.mapper.map(this.customerService.findCustomerByCpf(cpf), CustomerDTO.class));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<CustomerDTO>> filter(
            @RequestParam(value = "cpf", required = false) String cpf,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "customerStatus", required = false) String customerStatus
    ) {
        CustomerDTO customerDTO = CustomerDTO.builder()
                .cpf(cpf)
                .name(name)
                .email(email)
                .phone(phone)
                .customerStatus(customerStatus)
                .build();
        return ResponseEntity.ok().body(
                this.customerService.filterCustomer(customerDTO).stream().map(cliente ->
                        this.mapper.map(cliente, CustomerDTO.class)
                ).collect(Collectors.toList()));
    }

    @PatchMapping("/toggle-status/{cpf}")
    public ResponseEntity<Situation> toggleStatus(@PathVariable("cpf") String cpf) {
    	Situation statusCliente = this.customerService.toggleCustomerStatus(cpf);
        return ResponseEntity.ok().body(statusCliente);
    }

    @PutMapping("/update/{cpf}")
    public ResponseEntity<CustomerDTO> update(@PathVariable("cpf") String cpf,
                                              @Valid @RequestBody CustomerDTO customerDTO) {
        customerDTO.setCpf(cpf);
        return ResponseEntity.ok().body(this.mapper.map(this.customerService.updateCustomer(customerDTO), CustomerDTO.class));
    }

    @DeleteMapping("/delete/{cpf}")
    public ResponseEntity<?> delete(@PathVariable("cpf") String cpf) {
        this.customerService.deleteCustomer(cpf);
        return ResponseEntity.noContent().build();
    }
}
