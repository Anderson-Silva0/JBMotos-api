package com.jbmotos.services.impl;

import com.jbmotos.api.dto.DailyDataChart;
import com.jbmotos.model.entity.Repair;
import com.jbmotos.model.entity.Sale;
import com.jbmotos.model.repositories.RepairRepository;
import com.jbmotos.model.repositories.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyDataChartServiceImplTest {

    private static final ZoneId RECIFE_ZONE = ZoneId.of("America/Recife");

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private RepairRepository repairRepository;

    @InjectMocks
    private DailyDataChartServiceImpl service;

    private List<Sale> sales;
    private List<Repair> repairs;

    @BeforeEach
    void setUp() {
        this.sales = this.buildSales();
        this.repairs = this.buildRepairs();
    }

    @Test
    void getDailyChartData_shouldAggregateSalesByDayOfMonth() {
        when(this.saleRepository.getSalesCurrentMonth(any(), any())).thenReturn(this.sales);
        when(this.repairRepository.getRepairsCurrentMonth(any(), any())).thenReturn(List.of());

        Map<Integer, Long> expectedSalesByDay = this.sales.stream()
                .collect(Collectors.groupingBy(sale -> sale.getCreatedAt().getDayOfMonth(), Collectors.counting()));

        List<DailyDataChart> result = this.service.getDailyChartData();

        assertNotNull(result);
        assertEquals(this.currentMonthDays(), result.size());

        result.forEach(chart -> {
            Long expectedSales = expectedSalesByDay.get(this.extractDayOfMonthFromMillis(chart.dataMillis()));
            assertEquals(expectedSales, chart.saleQuantity());
            assertNull(chart.repairQuantity());
        });
    }

    @Test
    void getDailyChartData_shouldAggregateRepairsByDayOfMonth() {
        when(this.saleRepository.getSalesCurrentMonth(any(), any())).thenReturn(List.of());
        when(this.repairRepository.getRepairsCurrentMonth(any(), any())).thenReturn(this.repairs);

        Map<Integer, Long> expectedRepairsByDay = this.repairs.stream()
                .collect(Collectors.groupingBy(repair -> repair.getCreatedAt().getDayOfMonth(), Collectors.counting()));

        List<DailyDataChart> result = this.service.getDailyChartData();

        assertNotNull(result);
        assertEquals(this.currentMonthDays(), result.size());

        result.forEach(chart -> {
            Long expectedRepairs = expectedRepairsByDay.get(this.extractDayOfMonthFromMillis(chart.dataMillis()));
            assertNull(chart.saleQuantity());
            assertEquals(expectedRepairs, chart.repairQuantity());
        });
    }

    @Test
    void getDailyChartData_shouldReturnEmptyCountsWhenNoDataExists() {
        when(this.saleRepository.getSalesCurrentMonth(any(), any())).thenReturn(List.of());
        when(this.repairRepository.getRepairsCurrentMonth(any(), any())).thenReturn(List.of());

        List<DailyDataChart> result = this.service.getDailyChartData();

        assertNotNull(result);
        assertEquals(this.currentMonthDays(), result.size());
        result.forEach(chart -> {
            assertNull(chart.saleQuantity());
            assertNull(chart.repairQuantity());
        });
    }

    @Test
    void getDailyChartData_shouldUseMidnightTimestampsInRecifeZone() {
        when(this.saleRepository.getSalesCurrentMonth(any(), any())).thenReturn(this.sales);
        when(this.repairRepository.getRepairsCurrentMonth(any(), any())).thenReturn(this.repairs);

        List<DailyDataChart> result = this.service.getDailyChartData();

        result.forEach(chart -> {
            LocalDateTime localDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(chart.dataMillis()), RECIFE_ZONE);
            assertEquals(0, localDate.getHour());
            assertEquals(0, localDate.getMinute());
            assertEquals(0, localDate.getSecond());
            assertEquals(0, localDate.getNano());
        });
    }

    private List<Sale> buildSales() {
        return List.of(
                this.buildSale(1, 12, 30, 0),
                this.buildSale(1, 14, 10, 5),
                this.buildSale(3, 9, 15, 5),
                this.buildSale(3, 20, 0, 0),
                this.buildSale(10, 13, 31, 45)
        );
    }

    private List<Repair> buildRepairs() {
        return List.of(
                this.buildRepair(2, 8, 15, 0),
                this.buildRepair(2, 11, 40, 28),
                this.buildRepair(4, 16, 37, 32),
                this.buildRepair(10, 7, 20, 12),
                this.buildRepair(10, 10, 33, 44),
                this.buildRepair(15, 9, 10, 10)
        );
    }

    private Sale buildSale(int dayOfMonth, int hour, int minute, int second) {
        return Sale.builder().createdAt(this.currentMonthDate(dayOfMonth, hour, minute, second)).build();
    }

    private Repair buildRepair(int dayOfMonth, int hour, int minute, int second) {
        return Repair.builder().createdAt(this.currentMonthDate(dayOfMonth, hour, minute, second)).build();
    }

    private LocalDateTime currentMonthDate(int dayOfMonth, int hour, int minute, int second) {
        LocalDateTime now = LocalDateTime.now();
        int safeDay = Math.min(dayOfMonth, now.toLocalDate().lengthOfMonth());
        return now.withDayOfMonth(safeDay)
                .withHour(hour)
                .withMinute(minute)
                .withSecond(second)
                .withNano(0);
    }

    private int currentMonthDays() {
        return LocalDateTime.now().toLocalDate().lengthOfMonth();
    }

    private int extractDayOfMonthFromMillis(long epochMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), RECIFE_ZONE).getDayOfMonth();
    }
}
