package com.jbmotos.model.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jbmotos.model.entity.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {

    Optional<Supplier> findSupplierByCnpj(String cnpj);

    boolean existsSupplierByCnpj(String cnpj);

    void deleteSupplierByCnpj(String cnpj);

    boolean existsSupplierByAddressId(Integer addressId);

    List<Supplier> findByCnpjNot(String cnpj);
}
