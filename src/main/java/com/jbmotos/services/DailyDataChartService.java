package com.jbmotos.services;

import com.jbmotos.api.dto.DailyDataChart;

import java.util.List;

public interface DailyDataChartService {

    List<DailyDataChart> getDadosDoGraficoDiario();
}
