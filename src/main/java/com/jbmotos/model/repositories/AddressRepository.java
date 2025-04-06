package com.jbmotos.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jbmotos.model.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Integer> {

}
