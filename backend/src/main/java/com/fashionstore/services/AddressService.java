package com.fashionstore.services;

import com.fashionstore.dto.request.AddressRequest;
import com.fashionstore.dto.response.AdminAddressResponse;
import com.fashionstore.dto.response.AddressResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.exceptions.ValidationException;
import com.fashionstore.models.User;
import org.springframework.stereotype.Service;
import com.fashionstore.models.Address;
import com.fashionstore.repositories.AddressRepository;
import com.fashionstore.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public AddressResponse addAddress(AddressRequest addressRequest) {
        List<User> users = findRequestedUsers(addressRequest);
        return AddressResponse.from(findOrCreateAndLink(addressRequest, users));
    }

    @Transactional
    public AddressResponse addAddress(Authentication authentication, AddressRequest addressRequest) {
        User currentUser = currentUserService.findCurrentUser(authentication);
        return AddressResponse.from(findOrCreateAndLink(addressRequest, List.of(currentUser)));
    }

    @Transactional(readOnly = true)
    public AddressResponse getAddressById(Long id) {
        Optional<Address> address = addressRepository.findById(id);
        return  address.map(AddressResponse::from).orElseThrow(() -> new NotFoundException("Address", id));
    }

    @Transactional(readOnly = true)
    public AddressResponse getAddressById(Authentication authentication, Long id) {
        var currentUser = currentUserService.findCurrentUser(authentication);
        return addressRepository.findByIdAndUsersId(id, currentUser.getId())
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
                AdminFilterSpecification.create(AdminSearchFields.ADDRESSES, search, filterColumn, filterValue),
                PageRequestFactory.create(page, size)
        ), AddressResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminAddressResponse> getPagedAdminAddresses(int page, int size, String search, String filterColumn, String filterValue) {
        if (!AdminFilterSpecification.hasFilters(search, filterColumn, filterValue)) {
            return PageResponse.from(addressRepository.findAll(PageRequestFactory.create(page, size)), AdminAddressResponse::from);
        }
        return PageResponse.from(addressRepository.findAll(
                AdminFilterSpecification.create(AdminSearchFields.ADDRESSES, search, filterColumn, filterValue),
                PageRequestFactory.create(page, size)
        ), AdminAddressResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<AddressResponse> getPagedAddresses(Authentication authentication, int page, int size) {
        var currentUser = currentUserService.findCurrentUser(authentication);
        return PageResponse.from(addressRepository.findByUsersId(currentUser.getId(), PageRequestFactory.create(page, size)), AddressResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<AddressResponse> getPagedAddressesByUserId(Long userId, int page, int size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User", userId);
        }
        return PageResponse.from(addressRepository.findByUsersId(userId, PageRequestFactory.create(page, size)), AddressResponse::from);
    }

    @Transactional
    public AddressResponse updateAddress(Long id, AddressRequest addressRequest){
        Address updatedAddress = addressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Address", id));
        applyAddressRequest(updatedAddress, addressRequest);
        if (addressRequest.getUserIds() != null) {
            syncUsers(updatedAddress, addressRequest.getUserIds());
        } else if (addressRequest.getUserId() != null) {
            linkUser(updatedAddress, findRequestedUser(addressRequest.getUserId()));
        }
        return AddressResponse.from(addressRepository.save(updatedAddress));
    }

    @Transactional
    public AddressResponse updateAddress(Authentication authentication, Long id, AddressRequest addressRequest){
        User currentUser = currentUserService.findCurrentUser(authentication);
        Address oldAddress = addressRepository.findByIdAndUsersId(id, currentUser.getId())
                .orElseThrow(() -> new NotFoundException("Address", id));
        unlinkUser(oldAddress, currentUser);
        Address newAddress = findOrCreateAndLink(addressRequest, List.of(currentUser));
        deleteIfUnlinked(oldAddress);
        return AddressResponse.from(newAddress);
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
        User currentUser = currentUserService.findCurrentUser(authentication);
        Address address = addressRepository.findByIdAndUsersId(id, currentUser.getId())
                .orElseThrow(() -> new NotFoundException("Address", id));
        unlinkUser(address, currentUser);
        deleteIfUnlinked(address);
    }

    private void applyAddressRequest(Address address, AddressRequest addressRequest) {
        address.setCity(normalize(addressRequest.getCity()));
        address.setAddressLine(normalize(addressRequest.getAddressLine()));
        address.setCountry(normalize(addressRequest.getCountry()));
        address.setRegion(normalize(addressRequest.getRegion()));
        address.setPostalCode(addressRequest.getPostalCode());
    }

    private Address findOrCreateAndLink(AddressRequest addressRequest, List<User> users) {
        String country = normalize(addressRequest.getCountry());
        String region = normalize(addressRequest.getRegion());
        String city = normalize(addressRequest.getCity());
        String addressLine = normalize(addressRequest.getAddressLine());

        Address address = addressRepository.findByCountryAndRegionAndCityAndPostalCodeAndAddressLine(
                        country, region, city, addressRequest.getPostalCode(), addressLine)
                .orElseGet(() -> {
                    Address newAddress = new Address();
                    newAddress.setCountry(country);
                    newAddress.setRegion(region);
                    newAddress.setCity(city);
                    newAddress.setPostalCode(addressRequest.getPostalCode());
                    newAddress.setAddressLine(addressLine);
                    return newAddress;
                });
        users.forEach(user -> linkUser(address, user));
        return addressRepository.save(address);
    }

    private List<User> findRequestedUsers(AddressRequest addressRequest) {
        if (addressRequest.getUserIds() != null) {
            return addressRequest.getUserIds().stream().map(this::findRequestedUser).toList();
        }
        return List.of(findRequestedUser(addressRequest.getUserId()));
    }

    private User findRequestedUser(Long userId) {
        if (userId == null) {
            throw new ValidationException("User reference is required");
        }
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User", userId));
    }

    private void linkUser(Address address, User user) {
        if (address.getUsers().stream().noneMatch(existing -> existing.getId().equals(user.getId()))) {
            address.getUsers().add(user);
        }
    }

    private void unlinkUser(Address address, User user) {
        address.getUsers().removeIf(existing -> existing.getId().equals(user.getId()));
        addressRepository.save(address);
    }

    private void syncUsers(Address address, List<Long> userIds) {
        List<User> users = userIds.stream().map(this::findRequestedUser).collect(Collectors.toList());
        address.getUsers().clear();
        users.forEach(user -> linkUser(address, user));
    }

    private void deleteIfUnlinked(Address address) {
        if (address.getUsers().isEmpty()) {
            addressRepository.delete(address);
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
