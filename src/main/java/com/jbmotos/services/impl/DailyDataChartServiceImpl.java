package com.jbmotos.services.impl;

import com.jbmotos.api.dto.DailyDataChart;
import com.jbmotos.model.entity.Repair;
import com.jbmotos.model.entity.Sale;
import com.jbmotos.model.repositories.RepairRepository;
import com.jbmotos.model.repositories.SaleRepository;
import com.jbmotos.services.DailyDataChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DailyDataChartServiceImpl implements DailyDataChartService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private RepairRepository repairRepository;

    @Override
    public List<DailyDataChart> getDailyChartData() {
        List<DailyDataChart> result = new LinkedList<>();
        DailyDataChart dailyDataChart;

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        LocalDateTime beginDate = this.toLocalDateTime(calendar.getTime());

        calendar.add(Calendar.MONTH, 1);

        LocalDateTime endDate = this.toLocalDateTime(calendar.getTime());

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

        calendar.add(Calendar.MONTH, -1);

        long beginDateInMillis = calendar.getTimeInMillis();
        long endDateInMillis = endDate.atZone(ZoneId.of("America/Recife")).toInstant().toEpochMilli();

        while (beginDateInMillis < endDateInMillis) {
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            Long saleQuantity = dayMonthSaleQuantityMap.get(dayOfMonth);
            Long repairQuantity = dayMonthRepairQuantityMap.get(dayOfMonth);

            dailyDataChart = new DailyDataChart(calendar.getTime().getTime(), saleQuantity, repairQuantity);

            result.add(dailyDataChart);

            calendar.add(Calendar.DAY_OF_MONTH, 1);
            beginDateInMillis = calendar.getTimeInMillis();
        }

        return result;
    }

    private LocalDateTime toLocalDateTime(Date data) {
        return data.toInstant()
                .atZone(ZoneId.of("America/Recife"))
                .toLocalDateTime();
    }

}
