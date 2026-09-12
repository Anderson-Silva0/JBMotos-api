package com.jbmotos.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.jbmotos.api.dto.DailyDataChart;
import com.jbmotos.services.DailyDataChartService;

@ExtendWith(MockitoExtension.class)
class DailyDataChartControllerTest {

    @InjectMocks
    private DailyDataChartController controller;

    @Mock
    private DailyDataChartService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Deve retornar dados do gráfico diário")
    void getChartData() throws Exception {
        when(service.getDailyChartData()).thenReturn(List.of(new DailyDataChart(1704067200000L, 10L, 20L)));

        mockMvc.perform(get("/api/daily-data-chart")).andExpect(status().isOk());
    }
}
