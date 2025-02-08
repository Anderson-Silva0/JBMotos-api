package com.jbmotos.services.impl;

import com.jbmotos.api.dto.DailyDataChart;
import com.jbmotos.model.entity.Servico;
import com.jbmotos.model.entity.Venda;
import com.jbmotos.model.repositories.ServicoRepository;
import com.jbmotos.model.repositories.VendaRepository;
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
    private VendaRepository vendaRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    @Override
    public List<DailyDataChart> getDadosDoGraficoDiario() {
        List<DailyDataChart> result = new LinkedList<>();
        DailyDataChart dailyDataChart;

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        LocalDateTime dataInicio = toLocalDateTime(calendar.getTime());

        calendar.add(Calendar.MONTH, 1);

        LocalDateTime dataFim = toLocalDateTime(calendar.getTime());

        List<Venda> vendasMesAtual = vendaRepository.getVendasDoMesAtual(dataInicio, dataFim);
        List<Servico> servicosMesAtual = servicoRepository.getServicosDoMesAtual(dataInicio, dataFim);

        Map<Integer, Long> diaMesQtdVendaMap = vendasMesAtual.stream()
                .collect(Collectors.groupingBy(
                        venda -> venda.getDataHoraCadastro().getDayOfMonth(),
                        Collectors.counting()
                ));

        Map<Integer, Long> diaMesQtdServicoMap = servicosMesAtual.stream()
                .collect(Collectors.groupingBy(
                        servico -> servico.getDataHoraCadastro().getDayOfMonth(),
                        Collectors.counting()
                ));

        calendar.add(Calendar.MONTH, -1);

        long dataInicioMillis = calendar.getTimeInMillis();
        long dataFimMillis = dataFim.atZone(ZoneId.of("America/Recife")).toInstant().toEpochMilli();

        while (dataInicioMillis < dataFimMillis) {
            int diaDoMes = calendar.get(Calendar.DAY_OF_MONTH);
            Long qtdVenda = diaMesQtdVendaMap.get(diaDoMes);
            Long qtdServico = diaMesQtdServicoMap.get(diaDoMes);

            dailyDataChart = new DailyDataChart(calendar.getTime().getTime(), qtdVenda, qtdServico);

            result.add(dailyDataChart);

            calendar.add(Calendar.DAY_OF_MONTH, 1);
            dataInicioMillis = calendar.getTimeInMillis();
        }

        return result;
    }

    private LocalDateTime toLocalDateTime(Date data) {
        return data.toInstant()
                .atZone(ZoneId.of("America/Recife"))
                .toLocalDateTime();
    }

}
