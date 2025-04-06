package com.jbmotos.services;

import java.util.List;

import com.jbmotos.api.dto.RepairDTO;
import com.jbmotos.model.entity.Repair;

public interface RepairService {

    Repair saveRepair(RepairDTO repairDTO);

    List<Repair> findAllRepairs();

    Repair findRepairById(Integer repairId);
    
    List<Repair> filterRepair(RepairDTO repairDTO);

    Repair findRepairBySaleId(Integer saleId);

    List<Repair> findRepairByEmployeeCpf(String employeeCpf);

    Repair updateRepair(RepairDTO repairDTO);

    void deleteRepair(Integer repairId);

    void validateRepair(Integer repairId);
}
