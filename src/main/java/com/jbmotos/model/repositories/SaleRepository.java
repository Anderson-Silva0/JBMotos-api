package com.jbmotos.model.repositories;

import com.jbmotos.model.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Integer> {

    @Query("SELECT v FROM Sale v WHERE v.createdAt >= :beginDate AND v.createdAt < :endDate")
    List<Sale> getSalesCurrentMonth(@Param("beginDate") LocalDateTime beginDate, @Param("endDate") LocalDateTime endDate);
}