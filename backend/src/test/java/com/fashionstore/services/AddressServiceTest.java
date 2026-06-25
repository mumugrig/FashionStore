package com.fashionstore.services;

import com.fashionstore.dto.response.AddressResponse;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.repositories.AddressRepository;
import com.fashionstore.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

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
    @Mock private AddressRepository addressRepositoryMock;
    @Mock private UserRepository userRepositoryMock;
    @Mock private CurrentUserService currentUserServiceMock;
    private AddressService objectUnderTest;

    @BeforeEach
    void setUp() {
        objectUnderTest = new AddressService(addressRepositoryMock, userRepositoryMock, currentUserServiceMock);
    }

    @Test
    void addAddress_whenUserExists_returnsCreatedAddress() {
        var user = user(1L, "address-service@example.com");
        var createdAddress = address(1L, user, "Kyiv");

        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(addressRepositoryMock.save(any())).thenReturn(createdAddress);

        AddressResponse response = objectUnderTest.addAddress(addressRequest(user.getId(), "Kyiv"));

        assertEquals(user.getId(), response.getUserId(), "Created address should belong to the requested user");
    }

    @Test
    void updateAddress_whenAddressExists_returnsUpdatedAddress() {
        var user = user(1L, "address-service@example.com");
        var createdAddress = address(1L, user, "Kyiv");
        var updatedAddress = address(1L, user, "Lviv");

        when(addressRepositoryMock.findById(1L)).thenReturn(Optional.of(createdAddress));
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(addressRepositoryMock.save(createdAddress)).thenReturn(updatedAddress);

        AddressResponse response = objectUnderTest.updateAddress(1L, addressRequest(user.getId(), "Lviv"));

        assertEquals("Lviv", response.getCity(), "Updated address should contain the saved city");
    }

    @Test
    void updateAddress_whenAuthenticatedUserDoesNotOwnAddress_throwsNotFoundException() {
        var user = user(1L, "address-owner@example.com");
        Authentication authentication = authentication(user.getId());
        when(currentUserServiceMock.findCurrentUser(authentication)).thenReturn(user);
        when(addressRepositoryMock.findByIdAndUserId(1L, user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> objectUnderTest.updateAddress(authentication, 1L, addressRequest(99L, "Lviv")));
    }

    @Test
    void getPagedAddresses_whenAddressesExist_returnsPagedAddresses() {
        var user = user(1L, "address-service@example.com");
        var address = address(1L, user, "Lviv");

        when(addressRepositoryMock.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(address)));

        var response = objectUnderTest.getPagedAddresses(1, 20);

        assertFalse(response.getContent().isEmpty(), "Address page should contain repository results");
    }

    @Test
    void getPagedAddresses_whenAuthenticated_returnsOnlyCurrentUserAddresses() {
        var user = user(1L, "address-current@example.com");
        var address = address(1L, user, "Lviv");
        Authentication authentication = authentication(user.getId());

        when(currentUserServiceMock.findCurrentUser(authentication)).thenReturn(user);
        when(addressRepositoryMock.findByUserId(org.mockito.ArgumentMatchers.eq(user.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(address)));

        var response = objectUnderTest.getPagedAddresses(authentication, 1, 20);

        assertEquals(user.getId(), response.getContent().get(0).getUserId(), "Addresses should be scoped to the current user");
    }

    @Test
    void deleteAddress_whenAddressExists_deletesAddress() {
        when(addressRepositoryMock.existsById(1L)).thenReturn(true);

        objectUnderTest.deleteAddress(1L);

        verify(addressRepositoryMock).deleteById(1L);
    }

    @Test
    void deleteAddress_whenAuthenticatedUserDoesNotOwnAddress_throwsNotFoundException() {
        var user = user(1L, "address-owner-delete@example.com");
        Authentication authentication = authentication(user.getId());
        when(currentUserServiceMock.findCurrentUser(authentication)).thenReturn(user);
        when(addressRepositoryMock.findByIdAndUserId(1L, user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> objectUnderTest.deleteAddress(authentication, 1L));
    }

    @Test
    void getAddressById_whenAddressIsMissing_throwsNotFoundException() {
        when(addressRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> objectUnderTest.getAddressById(1L));
    }

    private Authentication authentication(Long userId) {
        return new TestingAuthenticationToken("user-" + userId + "@example.com", null);
    }
}
