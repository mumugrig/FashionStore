package com.fashionstore.controllers;

import com.fashionstore.services.AddressService;
import com.fashionstore.controllers.admin.AdminAddressController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest extends ControllerTestSupport {
    @Mock private AddressService addressServiceMock;
    private AdminAddressController objectUnderTest;

    @BeforeEach
    void setUp() {
        objectUnderTest = new AdminAddressController(addressServiceMock);
    }

    @Test
    void createAddress_whenRequestIsValid_returnsCreatedAddress() {
        when(addressServiceMock.addAddress(any())).thenReturn(addressResponse(1L, 1L, "Kyiv"));

        var response = objectUnderTest.createAddress(addressRequest(1L, "Kyiv"));

        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Address creation should return HTTP 201");
        assertEquals("Kyiv", response.getBody().getCity(), "Created address city should match service response");
    }

    @Test
    void updateAddress_whenAddressExists_returnsUpdatedAddress() {
        when(addressServiceMock.updateAddress(org.mockito.ArgumentMatchers.eq(1L), any()))
                .thenReturn(addressResponse(1L, 1L, "Lviv"));

        var response = objectUnderTest.updateAddress(1L, addressRequest(1L, "Lviv"));

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Address update should return HTTP 200");
        assertEquals("Lviv", response.getBody().getCity(), "Updated address city should match service response");
    }

    @Test
    void getAddressById_whenAddressExists_returnsAddress() {
        when(addressServiceMock.getAddressById(1L)).thenReturn(addressResponse(1L, 1L, "Lviv"));

        var response = objectUnderTest.getAddressById(1L);

        assertEquals(1L, response.getBody().getId(), "Fetched address id should match requested address");
    }

    @Test
    void getAddresses_whenPageIsRequested_returnsPagedAddresses() {
        when(addressServiceMock.getPagedAddresses(1, 20)).thenReturn(pageResponse(addressResponse(1L, 1L, "Lviv")));

        var response = objectUnderTest.getPagedAddresses(1, 20);

        assertEquals(1, response.getBody().getContent().size(), "Address page should contain service results");
    }

    @Test
    void deleteAddress_whenAddressExists_returnsNoContent() {
        var response = objectUnderTest.deleteAddress(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "Address deletion should return HTTP 204");
        verify(addressServiceMock).deleteAddress(1L);
    }
}
