package com.jbmotos.services;

import java.util.List;

import com.jbmotos.api.dto.AddressDTO;
import com.jbmotos.model.entity.Address;

public interface AddressService {

    Address saveAddress(AddressDTO addressDTO);

    List<Address> findAllAddress();

    Address findAddressById(Integer id);

    Address updateAddress(AddressDTO addressDTO);

    void deleteAddressById(Integer id);

    void validateAddress(Integer id);
}
