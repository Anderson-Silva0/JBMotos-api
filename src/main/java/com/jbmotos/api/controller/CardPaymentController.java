package com.jbmotos.api.controller;

import com.jbmotos.api.dto.CardPaymentDTO;
import com.jbmotos.services.CardPaymentService;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/card-payment")
public class CardPaymentController {

    @Autowired
    private CardPaymentService cardPaymentService;

    @Autowired
    private ModelMapper mapper;


    @PostMapping
    public ResponseEntity<CardPaymentDTO> save(@Valid @RequestBody CardPaymentDTO cardPaymentDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                this.mapper.map(this.cardPaymentService.saveCardPayment(cardPaymentDTO), CardPaymentDTO.class)
        );
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<CardPaymentDTO>> findAll() {
        return ResponseEntity.ok().body(
                this.cardPaymentService.findAllCardPayments().stream().map(cardPayment ->
                        this.mapper.map(cardPayment, CardPaymentDTO.class)
                ).collect(Collectors.toList()));
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<CardPaymentDTO> findById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok().body(
                this.mapper.map(this.cardPaymentService.findCardPaymentById(id), CardPaymentDTO.class)
        );
    }

    @GetMapping("/find-by-sale-id/{saleId}")
    public ResponseEntity<CardPaymentDTO> findBySaleId(@PathVariable("saleId") Integer saleId) {
        return ResponseEntity.ok().body(
               this.mapper.map(this.cardPaymentService.findCardPaymentBySaleId(saleId), CardPaymentDTO.class)
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CardPaymentDTO> update(@PathVariable("id") Integer id,
                                                 @Valid @RequestBody CardPaymentDTO cardPaymentDTO) {
        cardPaymentDTO.setId(id);
        return ResponseEntity.ok().body(
                this.mapper.map(this.cardPaymentService.updateCardPayment(cardPaymentDTO), CardPaymentDTO.class)
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
        this.cardPaymentService.deleteCardPayment(id);
        return ResponseEntity.noContent().build();
    }
}
