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
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DailyDataChartServiceImplTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private RepairRepository repairRepository;

    @InjectMocks
    private DailyDataChartServiceImpl dailyDataChartService;

    private List<Sale> saleList;

    private List<Repair> repairList;

    private final ZoneId recifeZone = ZoneId.of("America/Recife");

    @BeforeEach
    void setUp() {
        this.saleList = this.getSaleList();
        this.repairList = this.getRepairList();
    }

    private List<Sale> getSaleList() {
        return List.of(
                this.buildSale(1, 12, 30, 30),
                this.buildSale(1, 11, 40, 28),
                this.buildSale(3, 16, 37, 32),
                this.buildSale(3, 19, 49, 51),
                this.buildSale(20, 13, 31, 45)
        );
    }

    private Sale buildSale(int day, int hour, int minute, int second) {
        return Sale.builder()
                .createdAt(LocalDateTime.of(2025, 6, day, hour, minute, second))
                .build();
    }

    private List<Repair> getRepairList() {
        return List.of(
                buildRepair(1, 12, 30, 30),
                buildRepair(2, 11, 40, 28),
                buildRepair(4, 16, 37, 32),
                buildRepair(4, 19, 49, 51),
                buildRepair(4, 13, 31, 45),
                buildRepair(6, 9, 15, 10),
                buildRepair(7, 14, 25, 5),
                buildRepair(7, 8, 22, 17),
                buildRepair(10, 10, 33, 44),
                buildRepair(10, 7, 20, 12)
        );
    }

    private Repair buildRepair(int day, int hour, int minute, int second) {
        return Repair.builder()
                .createdAt(LocalDateTime.of(2025, 6, day, hour, minute, second))
                .build();
    }

    @Test
    void getDailyChartDataWithSalesAggregatesByDay() {

        Map<Long, Long> dayMonthSaleQuantityMap = this.saleList.stream()
                .collect(Collectors.groupingBy(
                        sale -> {
                            LocalDateTime createdAt = sale.getCreatedAt()
                                    .withHour(0)
                                    .withMinute(0)
                                    .withSecond(0)
                                    .withNano(0);
                            return createdAt.atZone(this.recifeZone).toInstant().toEpochMilli();
                        },
                        Collectors.counting()
                ));

        when(this.saleRepository.getSalesCurrentMonth(any(), any())).thenReturn(this.saleList);

        List<DailyDataChart> dailyChartData = this.dailyDataChartService.getDailyChartData();

        assertNotNull(dailyChartData);

        assertFalse(dailyChartData.isEmpty());

        LocalDateTime today = LocalDateTime.now();
        int daysInThisMonth = today.toLocalDate().lengthOfMonth();
        assertEquals(daysInThisMonth, dailyChartData.size());

        dailyChartData.stream()
                .filter(chartData -> Objects.nonNull(chartData.saleQuantity()))
                .forEach(activeChartData -> {
                    Long expected = dayMonthSaleQuantityMap.get(activeChartData.dataMillis());
                    assertEquals(expected, activeChartData.saleQuantity());
                });
    }

    @Test
    void getDailyChartDataWithRepairsAggregatesByDay() {

        Map<Long, Long> dayMonthRepairQuantityMap = this.repairList.stream()
                .collect(Collectors.groupingBy(
                        repair -> {
                            LocalDateTime createdAt = repair.getCreatedAt()
                                    .withHour(0)
                                    .withMinute(0)
                                    .withSecond(0)
                                    .withNano(0);
                            return createdAt.atZone(this.recifeZone).toInstant().toEpochMilli();
                        },
                        Collectors.counting()
                ));

        when(this.repairRepository.getRepairsCurrentMonth(any(), any())).thenReturn(this.repairList);

        List<DailyDataChart> dailyChartData = this.dailyDataChartService.getDailyChartData();

        assertNotNull(dailyChartData);

        assertFalse(dailyChartData.isEmpty());

        LocalDateTime today = LocalDateTime.now();
        int daysInThisMonth = today.toLocalDate().lengthOfMonth();
        assertEquals(daysInThisMonth, dailyChartData.size());

        dailyChartData.stream()
                .filter(chartData -> Objects.nonNull(chartData.saleQuantity()))
                .forEach(activeChartData -> {
                    Long expected = dayMonthRepairQuantityMap.get(activeChartData.dataMillis());
                    assertEquals(expected, activeChartData.saleQuantity());
                });
    }

    @Test
    void getDailyChartDataWithEmptySalesAndRepairs() {
        when(this.saleRepository.getSalesCurrentMonth(any(), any())).thenReturn(List.of());
        when(this.repairRepository.getRepairsCurrentMonth(any(), any())).thenReturn(List.of());

        List<DailyDataChart> dailyChartData = this.dailyDataChartService.getDailyChartData();

        assertNotNull(dailyChartData);
        assertFalse(dailyChartData.isEmpty());

        LocalDateTime today = LocalDateTime.now();
        int daysInThisMonth = today.toLocalDate().lengthOfMonth();
        assertEquals(daysInThisMonth, dailyChartData.size());

        dailyChartData.forEach(chartData -> {
            assertNull(chartData.saleQuantity());
            assertNull(chartData.repairQuantity());
        });
    }

    @Test
    void getDailyChartDataShouldHaveDataMillisAtStartOfDay() {
        when(this.saleRepository.getSalesCurrentMonth(any(), any())).thenReturn(getSaleList());
        when(this.repairRepository.getRepairsCurrentMonth(any(), any())).thenReturn(getRepairList());

        List<DailyDataChart> dailyChartData = dailyDataChartService.getDailyChartData();

        dailyChartData.forEach(data -> {
            LocalDateTime localDate = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(data.dataMillis()),
                    this.recifeZone
            );

            assertEquals(0, localDate.getHour());
            assertEquals(0, localDate.getMinute());
            assertEquals(0, localDate.getSecond());
        });
    }


}
