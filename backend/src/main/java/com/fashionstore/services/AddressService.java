package com.fashionstore.services;

import com.fashionstore.dto.request.AddressRequest;
import com.fashionstore.dto.response.AddressResponse;
import org.springframework.stereotype.Service;
import com.fashionstore.models.Address;
import com.fashionstore.repositories.AddressRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AddressService {
    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public AddressResponse addAddress(AddressRequest addressRequest) {
        Address newAddress = new Address();
        newAddress.setCity(addressRequest.getCity());
        newAddress.setAddressLine(addressRequest.getAddressLine());
        newAddress.setCountry(addressRequest.getCountry());
        newAddress.setRegion(addressRequest.getRegion());
        newAddress.setPostalCode(addressRequest.getPostalCode());
        Address savedAddress = addressRepository.save(newAddress);
        return AddressResponse.from(savedAddress);
    }

    public AddressResponse getAddressById(Long id) {
        Optional<Address> address = addressRepository.findById(id);
        return  address.map(AddressResponse::from).orElse(null);
    }

    public List<AddressResponse> getAllAddresses() {
        return addressRepository.findAll()
                .stream()
                .map(AddressResponse::from)
                .collect(Collectors.toList());
    }

    public AddressResponse updateAddress(Long id, AddressRequest addressRequest){
        Optional<Address> address = addressRepository.findById(id);
        if (address.isPresent()) {
            Address updatedAddress = address.get();
            updatedAddress.setCity(addressRequest.getCity());
            updatedAddress.setAddressLine(addressRequest.getAddressLine());
            updatedAddress.setCountry(addressRequest.getCountry());
            updatedAddress.setRegion(addressRequest.getRegion());
            updatedAddress.setPostalCode(addressRequest.getPostalCode());
            Address savedAddress = addressRepository.save(updatedAddress);
            return AddressResponse.from(savedAddress);
        }
        return null;
    }

    public void deleteAddress(Long id){
        addressRepository.deleteById(id);
    }
}