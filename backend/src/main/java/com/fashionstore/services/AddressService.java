package com.fashionstore.services;

import com.fashionstore.dto.request.AddressRequest;
import com.fashionstore.dto.response.AddressResponse;
import com.fashionstore.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import com.fashionstore.models.Address;
import com.fashionstore.repositories.AddressRepository;
import com.fashionstore.repositories.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public AddressResponse addAddress(AddressRequest addressRequest) {
        Address newAddress = new Address();
        newAddress.setCity(addressRequest.getCity());
        newAddress.setAddressLine(addressRequest.getAddressLine());
        newAddress.setCountry(addressRequest.getCountry());
        newAddress.setRegion(addressRequest.getRegion());
        newAddress.setPostalCode(addressRequest.getPostalCode());
        newAddress.setUser(userRepository.findById(addressRequest.getUserId())
                .orElseThrow(() -> new NotFoundException("User", addressRequest.getUserId())));
        Address savedAddress = addressRepository.save(newAddress);
        return AddressResponse.from(savedAddress);
    }

    @Transactional(readOnly = true)
    public AddressResponse getAddressById(Long id) {
        Optional<Address> address = addressRepository.findById(id);
        return  address.map(AddressResponse::from).orElseThrow(() -> new NotFoundException("Address", id));
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getAllAddresses() {
        return addressRepository.findAll()
                .stream()
                .map(AddressResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressResponse updateAddress(Long id, AddressRequest addressRequest){
        Optional<Address> address = addressRepository.findById(id);
        if (address.isPresent()) {
            Address updatedAddress = address.get();
            updatedAddress.setCity(addressRequest.getCity());
            updatedAddress.setAddressLine(addressRequest.getAddressLine());
            updatedAddress.setCountry(addressRequest.getCountry());
            updatedAddress.setRegion(addressRequest.getRegion());
            updatedAddress.setPostalCode(addressRequest.getPostalCode());
            updatedAddress.setUser(userRepository.findById(addressRequest.getUserId())
                    .orElseThrow(() -> new NotFoundException("User", addressRequest.getUserId())));
            Address savedAddress = addressRepository.save(updatedAddress);
            return AddressResponse.from(savedAddress);
        }
        throw new NotFoundException("Address", id);
    }

    @Transactional
    public void deleteAddress(Long id){
        if (!addressRepository.existsById(id)) {
            throw new NotFoundException("Address", id);
        }
        addressRepository.deleteById(id);
    }
}
