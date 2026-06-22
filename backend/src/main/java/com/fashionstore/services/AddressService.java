package com.fashionstore.services;

import org.springframework.stereotype.Service;
import com.fashionstore.models.Address;
import com.fashionstore.repositories.AddressRepository;

@Service
public class AddressService {
    private final AddressRepository addressRepository;

    AddressService(AddressRepository addressRepository){
        this.addressRepository = addressRepository;
    }
    void addAddress(Address address) {
        addressRepository.save(address);
    }

}
