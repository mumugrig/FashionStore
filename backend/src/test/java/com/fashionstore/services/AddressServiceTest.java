package com.fashionstore.services;

import com.fashionstore.dto.response.AddressResponse;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.repositories.AddressRepository;
import com.fashionstore.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest extends ServiceTestSupport {
    @Mock private AddressRepository addressRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private AddressService addressService;

    @Test
    void createsUpdatesQueriesAndDeletesAddresses() {
        var user = user(1L, "address-service@example.com");
        var createdAddress = address(1L, user, "Kyiv");
        var updatedAddress = address(1L, user, "Lviv");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(addressRepository.save(any())).thenReturn(createdAddress);
        AddressResponse created = addressService.addAddress(addressRequest(user.getId(), "Kyiv"));

        when(addressRepository.findById(created.getId())).thenReturn(Optional.of(createdAddress));
        when(addressRepository.save(createdAddress)).thenReturn(updatedAddress);
        AddressResponse updated = addressService.updateAddress(created.getId(), addressRequest(user.getId(), "Lviv"));

        when(addressRepository.findAll()).thenReturn(List.of(updatedAddress));
        assertEquals(user.getId(), created.getUserId());
        assertEquals("Lviv", updated.getCity());
        assertFalse(addressService.getAllAddresses().isEmpty());

        addressService.deleteAddress(created.getId());
        verify(addressRepository).deleteById(created.getId());
    }

    @Test
    void throwsWhenAddressIsMissing() {
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> addressService.getAddressById(1L));
    }
}
