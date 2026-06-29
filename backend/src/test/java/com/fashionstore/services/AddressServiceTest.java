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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
        when(addressRepositoryMock.findByCountryAndRegionAndCityAndPostalCodeAndAddressLine(
                "Ukraine", "Region", "Kyiv", 1000, "123 Test Street")).thenReturn(Optional.empty());
        when(addressRepositoryMock.save(any())).thenReturn(createdAddress);

        AddressResponse response = objectUnderTest.addAddress(addressRequest(user.getId(), "Kyiv"));

        assertTrue(response.getUserIds().contains(user.getId()), "Created address should be linked to the requested user");
    }

    @Test
    void updateAddress_whenAdminUpdatesAddress_mutatesCanonicalRow() {
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
    void updateAddress_whenAdminProvidesUserIds_replacesLinkedUsers() {
        var removedUser = user(1L, "removed@example.com");
        var keptUser = user(2L, "kept@example.com");
        var addedUser = user(3L, "added@example.com");
        var address = address(1L, removedUser, "Kyiv");
        address.getUsers().add(keptUser);
        var request = addressRequest(null, "Lviv");
        request.setUserIds(List.of(keptUser.getId(), addedUser.getId()));

        when(addressRepositoryMock.findById(address.getId())).thenReturn(Optional.of(address));
        when(userRepositoryMock.findById(keptUser.getId())).thenReturn(Optional.of(keptUser));
        when(userRepositoryMock.findById(addedUser.getId())).thenReturn(Optional.of(addedUser));
        when(addressRepositoryMock.save(address)).thenReturn(address);

        AddressResponse response = objectUnderTest.updateAddress(address.getId(), request);

        assertFalse(response.getUserIds().contains(removedUser.getId()));
        assertTrue(response.getUserIds().contains(keptUser.getId()));
        assertTrue(response.getUserIds().contains(addedUser.getId()));
    }

    @Test
    void updateAddress_whenAuthenticatedUserDoesNotOwnAddress_throwsNotFoundException() {
        var user = user(1L, "address-owner@example.com");
        Authentication authentication = authentication(user.getId());
        when(currentUserServiceMock.findCurrentUser(authentication)).thenReturn(user);
        when(addressRepositoryMock.findByIdAndUsersId(1L, user.getId())).thenReturn(Optional.empty());

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
        when(addressRepositoryMock.findByUsersId(org.mockito.ArgumentMatchers.eq(user.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(address)));

        var response = objectUnderTest.getPagedAddresses(authentication, 1, 20);

        assertTrue(response.getContent().get(0).getUserIds().contains(user.getId()), "Addresses should be scoped to the current user");
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
        when(addressRepositoryMock.findByIdAndUsersId(1L, user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> objectUnderTest.deleteAddress(authentication, 1L));
    }

    @Test
    void deleteAddress_whenAuthenticatedUserSharesAddress_unlinksUserAndKeepsAddress() {
        var currentUser = user(1L, "delete-current@example.com");
        var otherUser = user(2L, "delete-other@example.com");
        var sharedAddress = address(1L, currentUser, "Kyiv");
        sharedAddress.getUsers().add(otherUser);
        Authentication authentication = authentication(currentUser.getId());

        when(currentUserServiceMock.findCurrentUser(authentication)).thenReturn(currentUser);
        when(addressRepositoryMock.findByIdAndUsersId(sharedAddress.getId(), currentUser.getId())).thenReturn(Optional.of(sharedAddress));

        objectUnderTest.deleteAddress(authentication, sharedAddress.getId());

        assertFalse(sharedAddress.getUsers().stream().anyMatch(user -> user.getId().equals(currentUser.getId())));
        assertTrue(sharedAddress.getUsers().stream().anyMatch(user -> user.getId().equals(otherUser.getId())));
        verify(addressRepositoryMock, never()).delete(sharedAddress);
    }

    @Test
    void deleteAddress_whenAuthenticatedUserIsLastLink_deletesAddressRow() {
        var currentUser = user(1L, "delete-last@example.com");
        var address = address(1L, currentUser, "Kyiv");
        Authentication authentication = authentication(currentUser.getId());

        when(currentUserServiceMock.findCurrentUser(authentication)).thenReturn(currentUser);
        when(addressRepositoryMock.findByIdAndUsersId(address.getId(), currentUser.getId())).thenReturn(Optional.of(address));

        objectUnderTest.deleteAddress(authentication, address.getId());

        assertTrue(address.getUsers().isEmpty());
        verify(addressRepositoryMock).delete(address);
    }

    @Test
    void addAddress_whenSameCanonicalAddressExists_linksUserToExistingRow() {
        var user = user(2L, "address-reuse@example.com");
        var existingAddress = address(1L, user(1L, "existing@example.com"), "Kyiv");

        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(addressRepositoryMock.findByCountryAndRegionAndCityAndPostalCodeAndAddressLine(
                "Ukraine", "Region", "Kyiv", 1000, "123 Test Street")).thenReturn(Optional.of(existingAddress));
        when(addressRepositoryMock.save(existingAddress)).thenReturn(existingAddress);

        AddressResponse response = objectUnderTest.addAddress(addressRequest(user.getId(), "Kyiv"));

        assertEquals(1L, response.getId());
        assertTrue(response.getUserIds().contains(user.getId()));
    }

    @Test
    void updateAddress_whenAuthenticated_repointsOnlyCurrentUser() {
        var currentUser = user(1L, "current@example.com");
        var otherUser = user(2L, "other@example.com");
        var oldAddress = address(1L, currentUser, "Kyiv");
        oldAddress.getUsers().add(otherUser);
        var newAddress = address(2L, currentUser, "Lviv");
        Authentication authentication = authentication(currentUser.getId());

        when(currentUserServiceMock.findCurrentUser(authentication)).thenReturn(currentUser);
        when(addressRepositoryMock.findByIdAndUsersId(oldAddress.getId(), currentUser.getId())).thenReturn(Optional.of(oldAddress));
        when(addressRepositoryMock.findByCountryAndRegionAndCityAndPostalCodeAndAddressLine(
                "Ukraine", "Region", "Lviv", 1000, "123 Test Street")).thenReturn(Optional.of(newAddress));
        when(addressRepositoryMock.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse response = objectUnderTest.updateAddress(authentication, oldAddress.getId(), addressRequest(99L, "Lviv"));

        assertEquals(2L, response.getId());
        assertFalse(oldAddress.getUsers().stream().anyMatch(user -> user.getId().equals(currentUser.getId())));
        assertTrue(oldAddress.getUsers().stream().anyMatch(user -> user.getId().equals(otherUser.getId())));
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
