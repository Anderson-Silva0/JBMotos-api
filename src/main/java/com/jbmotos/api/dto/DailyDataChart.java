package com.jbmotos.api.dto;

public record DailyDataChart(
        Long dataMillis, Long saleQuantity, Long repairQuantity
) {
}
