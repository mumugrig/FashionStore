package com.fashionstore.repositories;

import com.fashionstore.models.Address;
import com.fashionstore.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setPasswordHash("hashedPassword");
        userRepository.save(user);
    }

    @Test
    void save_whenAddressIsValid_persistsAddress() {
        Address address = address("USA", "California", "San Francisco", 94103, "123 Main St", user);

        Address savedAddress = addressRepository.save(address);

        assertNotNull(savedAddress.getId());
        assertEquals("San Francisco", savedAddress.getCity());
    }

    @Test
    void findById_whenAddressExists_returnsAddress() {
        Address address = address("USA", "NY", "New York", 10001, "456 Park Ave", user);
        Address savedAddress = addressRepository.save(address);

        Address foundAddress = addressRepository.findById(savedAddress.getId()).orElse(null);

        assertNotNull(foundAddress);
        assertEquals("New York", foundAddress.getCity());
    }

    @Test
    void findByUsersId_whenRecordsExist_returnsUserRecords() {
        User user2 = new User();
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setEmail("jane@example.com");
        user2.setPasswordHash("hashedPassword");
        userRepository.save(user2);

        Address address1 = address("USA", "TX", "Houston", 77001, "789 Road", user);
        addressRepository.save(address1);

        Address address2 = address("USA", "TX", "Dallas", 75201, "321 Ave", user);
        addressRepository.save(address2);

        Address address3 = address("Canada", "ON", "Toronto", 10001, "555 Street", user2);
        addressRepository.save(address3);

        List<Address> userAddresses = addressRepository.findByUsersId(user.getId());

        assertEquals(2, userAddresses.size());
        assertEquals(2, addressRepository.findByUsersId(user.getId(), PageRequest.of(0, 20)).getTotalElements());
        assertTrue(userAddresses.stream().allMatch(a -> a.getUsers().stream().anyMatch(u -> u.getId().equals(user.getId()))));
    }

    @Test
    void findByUsersId_whenRecordsAreMissing_returnsEmptyList() {
        User user3 = new User();
        user3.setFirstName("Bob");
        user3.setLastName("Brown");
        user3.setEmail("bob@example.com");
        user3.setPasswordHash("hashedPassword");
        userRepository.save(user3);

        List<Address> addresses = addressRepository.findByUsersId(user3.getId());

        assertEquals(0, addresses.size());
    }

    @Test
    void deleteById_whenAddressExists_removesAddress() {
        Address address = address("USA", "FL", "Miami", 33101, "999 Beach", user);
        Address savedAddress = addressRepository.save(address);

        addressRepository.deleteById(savedAddress.getId());

        assertFalse(addressRepository.existsById(savedAddress.getId()));
    }

    @Test
    void deleteLinksByUserId_whenUserHasRecords_removesUserLinks() {
        Address address = address("USA", "FL", "Miami", 33101, "999 Beach", user);
        addressRepository.save(address);

        addressRepository.deleteLinksByUserId(user.getId());

        assertTrue(addressRepository.findByUsersId(user.getId()).isEmpty());
        assertTrue(addressRepository.existsById(address.getId()));
    }

    @Test
    void count_whenAddressesExist_returnsAddressCount() {
        Address address1 = address("USA", "WA", "Seattle", 98101, "111 Pine", user);
        Address address2 = address("USA", "OR", "Portland", 97204, "222 Oak", user);

        addressRepository.save(address1);
        addressRepository.save(address2);

        long count = addressRepository.count();
        assertTrue(count >= 2);
    }

    @Test
    void findByCanonicalFields_whenAddressExists_returnsAddress() {
        Address savedAddress = addressRepository.save(address("USA", "FL", "Miami", 33101, "999 Beach", user));

        var foundAddress = addressRepository.findByCountryAndRegionAndCityAndPostalCodeAndAddressLine(
                "USA", "FL", "Miami", 33101, "999 Beach");

        assertTrue(foundAddress.isPresent());
        assertEquals(savedAddress.getId(), foundAddress.get().getId());
    }

    @Test
    void sharedAddress_canBeLinkedToMultipleUsers() {
        User user2 = new User();
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setEmail("jane-shared@example.com");
        user2.setPasswordHash("hashedPassword");
        userRepository.save(user2);

        Address sharedAddress = address("USA", "WA", "Seattle", 98101, "111 Pine", user);
        sharedAddress.getUsers().add(user2);
        Address savedAddress = addressRepository.save(sharedAddress);

        assertEquals(2, addressRepository.findById(savedAddress.getId()).orElseThrow().getUsers().size());
        assertEquals(1, addressRepository.findByUsersId(user.getId()).size());
        assertEquals(1, addressRepository.findByUsersId(user2.getId()).size());
    }

    private Address address(String country, String region, String city, int postalCode, String addressLine, User user) {
        Address address = new Address();
        address.setCountry(country);
        address.setRegion(region);
        address.setCity(city);
        address.setPostalCode(postalCode);
        address.setAddressLine(addressLine);
        address.getUsers().add(user);
        return address;
    }
}




