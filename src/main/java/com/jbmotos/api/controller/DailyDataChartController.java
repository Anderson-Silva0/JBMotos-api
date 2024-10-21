package com.jbmotos.api.controller;

import com.jbmotos.api.dto.DailyDataChart;
import com.jbmotos.services.DailyDataChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dailyDataChart")
public class DailyDataChartController {

    @Autowired
    private DailyDataChartService service;

    @GetMapping
    public ResponseEntity<List<DailyDataChart>> buscarDadosDoGrafico() {
        return ResponseEntity.ok().body(
                service.getDadosDoGraficoDiario());
    }
}
