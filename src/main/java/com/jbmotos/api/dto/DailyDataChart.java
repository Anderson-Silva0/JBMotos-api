package com.jbmotos.api.dto;

import java.util.Date;

public record DailyDataChart(
        Long dataMillis, Long qtdVenda, Long qtdServico
) {
}
