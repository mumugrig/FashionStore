package com.fashionstore.services;

import com.fashionstore.dto.request.AddressRequest;
import com.fashionstore.dto.response.AdminAddressResponse;
import com.fashionstore.dto.response.AddressResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import com.fashionstore.models.Address;
import com.fashionstore.repositories.AddressRepository;
import com.fashionstore.repositories.UserRepository;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public AddressResponse addAddress(AddressRequest addressRequest) {
        Address newAddress = new Address();
        applyAddressRequest(newAddress, addressRequest);
        newAddress.setUser(userRepository.findById(addressRequest.getUserId())
                .orElseThrow(() -> new NotFoundException("User", addressRequest.getUserId())));
        Address savedAddress = addressRepository.save(newAddress);
        return AddressResponse.from(savedAddress);
    }

    @Transactional
    public AddressResponse addAddress(Authentication authentication, AddressRequest addressRequest) {
        Address newAddress = new Address();
        applyAddressRequest(newAddress, addressRequest);
        newAddress.setUser(currentUserService.findCurrentUser(authentication));
        Address savedAddress = addressRepository.save(newAddress);
        return AddressResponse.from(savedAddress);
    }

    @Transactional(readOnly = true)
    public AddressResponse getAddressById(Long id) {
        Optional<Address> address = addressRepository.findById(id);
        return  address.map(AddressResponse::from).orElseThrow(() -> new NotFoundException("Address", id));
    }

    @Transactional(readOnly = true)
    public AddressResponse getAddressById(Authentication authentication, Long id) {
        var currentUser = currentUserService.findCurrentUser(authentication);
        return addressRepository.findByIdAndUserId(id, currentUser.getId())
                .map(AddressResponse::from)
                .orElseThrow(() -> new NotFoundException("Address", id));
    }

    @Transactional(readOnly = true)
    public PageResponse<AddressResponse> getPagedAddresses(int page, int size) {
        return PageResponse.from(addressRepository.findAll(PageRequestFactory.create(page, size)), AddressResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<AddressResponse> getPagedAddresses(int page, int size, String search, String filterColumn, String filterValue) {
        if (!AdminFilterSpecification.hasFilters(search, filterColumn, filterValue)) {
            return getPagedAddresses(page, size);
        }
        return PageResponse.from(addressRepository.findAll(
                AdminFilterSpecification.create(adminFields(), search, filterColumn, filterValue),
                PageRequestFactory.create(page, size)
        ), AddressResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminAddressResponse> getPagedAdminAddresses(int page, int size, String search, String filterColumn, String filterValue) {
        if (!AdminFilterSpecification.hasFilters(search, filterColumn, filterValue)) {
            return PageResponse.from(addressRepository.findAll(PageRequestFactory.create(page, size)), AdminAddressResponse::from);
        }
        return PageResponse.from(addressRepository.findAll(
                AdminFilterSpecification.create(adminFields(), search, filterColumn, filterValue),
                PageRequestFactory.create(page, size)
        ), AdminAddressResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<AddressResponse> getPagedAddresses(Authentication authentication, int page, int size) {
        var currentUser = currentUserService.findCurrentUser(authentication);
        return PageResponse.from(addressRepository.findByUserId(currentUser.getId(), PageRequestFactory.create(page, size)), AddressResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<AddressResponse> getPagedAddressesByUserId(Long userId, int page, int size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User", userId);
        }
        return PageResponse.from(addressRepository.findByUserId(userId, PageRequestFactory.create(page, size)), AddressResponse::from);
    }

    @Transactional
    public AddressResponse updateAddress(Long id, AddressRequest addressRequest){
        Optional<Address> address = addressRepository.findById(id);
        if (address.isPresent()) {
            Address updatedAddress = address.get();
            applyAddressRequest(updatedAddress, addressRequest);
            updatedAddress.setUser(userRepository.findById(addressRequest.getUserId())
                    .orElseThrow(() -> new NotFoundException("User", addressRequest.getUserId())));
            Address savedAddress = addressRepository.save(updatedAddress);
            return AddressResponse.from(savedAddress);
        }
        throw new NotFoundException("Address", id);
    }

    @Transactional
    public AddressResponse updateAddress(Authentication authentication, Long id, AddressRequest addressRequest){
        var currentUser = currentUserService.findCurrentUser(authentication);
        Address updatedAddress = addressRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new NotFoundException("Address", id));
        applyAddressRequest(updatedAddress, addressRequest);
        Address savedAddress = addressRepository.save(updatedAddress);
        return AddressResponse.from(savedAddress);
    }

    @Transactional
    public void deleteAddress(Long id){
        if (!addressRepository.existsById(id)) {
            throw new NotFoundException("Address", id);
        }
        addressRepository.deleteById(id);
    }

    @Transactional
    public void deleteAddresses(List<Long> ids) {
        ids.forEach(this::deleteAddress);
    }

    @Transactional
    public void deleteAddress(Authentication authentication, Long id){
        var currentUser = currentUserService.findCurrentUser(authentication);
        Address address = addressRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new NotFoundException("Address", id));
        addressRepository.delete(address);
    }

    private void applyAddressRequest(Address address, AddressRequest addressRequest) {
        address.setCity(addressRequest.getCity());
        address.setAddressLine(addressRequest.getAddressLine());
        address.setCountry(addressRequest.getCountry());
        address.setRegion(addressRequest.getRegion());
        address.setPostalCode(addressRequest.getPostalCode());
    }

    private Map<String, Function<Root<Address>, Expression<?>>> adminFields() {
        return Map.ofEntries(
                Map.entry("id", root -> root.get("id")),
                Map.entry("country", root -> root.get("country")),
                Map.entry("region", root -> root.get("region")),
                Map.entry("city", root -> root.get("city")),
                Map.entry("postalCode", root -> root.get("postalCode")),
                Map.entry("addressLine", root -> root.get("addressLine")),
                Map.entry("userId", root -> root.get("user").get("id")),
                Map.entry("userFirstName", root -> root.get("user").get("firstName")),
                Map.entry("userLastName", root -> root.get("user").get("lastName")),
                Map.entry("userName", root -> root.get("user").get("firstName")),
                Map.entry("userEmail", root -> root.get("user").get("email")),
                Map.entry("userPhoneNumber", root -> root.get("user").get("phoneNumber"))
        );
    }
}
