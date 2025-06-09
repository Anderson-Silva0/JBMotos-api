package com.jbmotos.services.impl;

import com.jbmotos.api.dto.DailyDataChart;
import com.jbmotos.model.entity.Repair;
import com.jbmotos.model.entity.Sale;
import com.jbmotos.model.repositories.RepairRepository;
import com.jbmotos.model.repositories.SaleRepository;
import com.jbmotos.services.DailyDataChartService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DailyDataChartServiceImpl implements DailyDataChartService {

    private final SaleRepository saleRepository;

    private final RepairRepository repairRepository;

    public DailyDataChartServiceImpl(SaleRepository saleRepository, RepairRepository repairRepository) {
        this.saleRepository = saleRepository;
        this.repairRepository = repairRepository;
    }

    @Override
    public List<DailyDataChart> getDailyChartData() {
        List<DailyDataChart> result = new LinkedList<>();
        DailyDataChart dailyDataChart;

        LocalDateTime beginDate = LocalDateTime.now()
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        LocalDateTime endDate = beginDate.plusMonths(1);

        List<Sale> currentMonthSales = this.saleRepository.getSalesCurrentMonth(beginDate, endDate);
        List<Repair> currentMonthRepairs = this.repairRepository.getRepairsCurrentMonth(beginDate, endDate);

        Map<Integer, Long> dayMonthSaleQuantityMap = currentMonthSales.stream()
                .collect(Collectors.groupingBy(
                        sale -> sale.getCreatedAt().getDayOfMonth(),
                        Collectors.counting()
                ));

        Map<Integer, Long> dayMonthRepairQuantityMap = currentMonthRepairs.stream()
                .collect(Collectors.groupingBy(
                        repair -> repair.getCreatedAt().getDayOfMonth(),
                        Collectors.counting()
                ));

        final ZoneId RECIFE_ZONE = ZoneId.of("America/Recife");

        for (LocalDateTime date = beginDate; date.isBefore(endDate); date = date.plusDays(1)) {
            int dayOfMonth = date.getDayOfMonth();
            Long saleQuantity = dayMonthSaleQuantityMap.get(dayOfMonth);
            Long repairQuantity = dayMonthRepairQuantityMap.get(dayOfMonth);

            Instant instantRecife = date.atZone(RECIFE_ZONE).toInstant();
            long epochMillisRecife = instantRecife.toEpochMilli();

            dailyDataChart = new DailyDataChart(epochMillisRecife, saleQuantity, repairQuantity);

            result.add(dailyDataChart);
        }

        return result;
    }

}
