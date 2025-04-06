package com.jbmotos.services;

import java.util.List;

import com.jbmotos.api.dto.SupplierDTO;
import com.jbmotos.model.entity.Supplier;
import com.jbmotos.model.enums.Situation;

public interface SupplierService {

    Supplier saveSupplier(SupplierDTO supplierDTO);

    List<Supplier> findAllSuppliers();

    Supplier findSupplierByCnpj(String cnpj);

    List<Supplier> filterSupplier(SupplierDTO supplierDTO);

    Situation toggleSupplierStatus(String cnpj);

    Supplier updateSupplier(SupplierDTO supplierDTO);

    void deleteSupplier(String cnpj);

    void validateSupplierCnpjToSave(String cnpj);

    boolean existsSupplierByAddress(Integer addressId);
}
