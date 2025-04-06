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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.jbmotos.api.dto.EmployeeDTO;
import com.jbmotos.model.entity.Employee;
import com.jbmotos.model.enums.Situation;
import com.jbmotos.services.EmployeeService;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public ResponseEntity<EmployeeDTO> save(@Valid @RequestBody EmployeeDTO employeeDTO) {
        Employee employee = this.employeeService.saveEmployee(employeeDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().buildAndExpand(employee).toUri();
        return ResponseEntity.created(uri).body( this.mapper.map(employee, EmployeeDTO.class) );
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<EmployeeDTO>> findAll() {
        return ResponseEntity.ok().body(
                this.employeeService.findAllEmployees().stream().map(employee ->
                        this.mapper.map(employee, EmployeeDTO.class)
                        ).collect(Collectors.toList()));
    }

    @GetMapping("/find/{cpf}")
    public ResponseEntity<EmployeeDTO> findByCpf(@PathVariable("cpf") String cpf) {
        return ResponseEntity.ok().body(
                this.mapper.map(this.employeeService.findEmployeeByCpf(cpf), EmployeeDTO.class));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<EmployeeDTO>> filter(
            @RequestParam(value = "cpf", required = false) String cpf,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "employeeStatus", required = false) String employeeStatus
    ) {
        EmployeeDTO employeeDTO = EmployeeDTO.builder()
                .cpf(cpf)
                .name(name)
                .phone(phone)
                .employeeStatus(employeeStatus)
                .build();
        return ResponseEntity.ok().body(
                this.employeeService.filterEmployee(employeeDTO).stream().map(employee ->
                        this.mapper.map(employee, EmployeeDTO.class)
                ).collect(Collectors.toList()));
    }

    @PatchMapping("/toggle-status/{cpf}")
    public ResponseEntity<Situation> toggleStatus(@PathVariable("cpf") String cpf) {
    	Situation employeeStatus = this.employeeService.toggleEmployeeStatus(cpf);
        return ResponseEntity.ok().body(employeeStatus);
    }

    @PutMapping("/update/{cpf}")
    public ResponseEntity<EmployeeDTO> update(@PathVariable("cpf") String cpf,
                                              @Valid @RequestBody EmployeeDTO employeeDTO) {
        employeeDTO.setCpf(cpf);
        return ResponseEntity.ok().body(
                this.mapper.map(this.employeeService.updateEmployee(employeeDTO), EmployeeDTO.class));
    }

    @DeleteMapping("/delete/{cpf}")
    public ResponseEntity<?> delete(@PathVariable("cpf") String cpf) {
        this.employeeService.deleteEmployee(cpf);
        return ResponseEntity.noContent().build();
    }
}
