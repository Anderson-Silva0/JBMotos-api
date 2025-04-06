package com.jbmotos.model.repositories;

import com.jbmotos.model.entity.Repair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RepairRepository extends JpaRepository<Repair, Integer> {

    Optional<Repair> findRepairBySaleId(Integer saleId);

    List<Repair> findRepairByEmployeeCpf(String employeeCpf);

    @Query("SELECT s FROM Repair s WHERE s.createdAt >= :beginDate AND s.createdAt < :endDate")
    List<Repair> getRepairsCurrentMonth(@Param("beginDate") LocalDateTime beginDate, @Param("endDate") LocalDateTime endDate);
}
