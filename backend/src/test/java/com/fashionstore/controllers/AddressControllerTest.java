package com.fashionstore.controllers;

import com.fashionstore.services.AddressService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest extends ControllerTestSupport {
    @Mock private AddressService addressService;
    @InjectMocks private AddressController addressController;

    @Test
    void createsUpdatesReadsListsAndDeletesAddresses() {
        when(addressService.addAddress(any())).thenReturn(addressResponse(1L, 1L, "Kyiv"));
        when(addressService.updateAddress(org.mockito.ArgumentMatchers.eq(1L), any())).thenReturn(addressResponse(1L, 1L, "Lviv"));
        when(addressService.getAddressById(1L)).thenReturn(addressResponse(1L, 1L, "Lviv"));
        when(addressService.getAllAddresses()).thenReturn(List.of(addressResponse(1L, 1L, "Lviv")));

        var created = addressController.createAddress(addressRequest(1L, "Kyiv"));
        var updated = addressController.updateAddress(1L, addressRequest(1L, "Lviv"));
        var fetched = addressController.getAddressById(1L);
        var listed = addressController.getAddresses();
        var deleted = addressController.deleteAddress(1L);

        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        assertEquals("Lviv", updated.getBody().getCity());
        assertEquals(1L, fetched.getBody().getId());
        assertEquals(1, listed.getBody().size());
        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());
        verify(addressService).deleteAddress(1L);
    }
}
