package com.fashionstore.repositories;

import com.fashionstore.models.Address;
import com.fashionstore.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
        Address address = new Address();
        address.setCountry("USA");
        address.setRegion("California");
        address.setCity("San Francisco");
        address.setPostalCode(94103);
        address.setAddressLine("123 Main St");
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);

        assertNotNull(savedAddress.getId());
        assertEquals("San Francisco", savedAddress.getCity());
    }

    @Test
    void findById_whenAddressExists_returnsAddress() {
        Address address = new Address();
        address.setCountry("USA");
        address.setRegion("NY");
        address.setCity("New York");
        address.setPostalCode(10001);
        address.setAddressLine("456 Park Ave");
        address.setUser(user);
        Address savedAddress = addressRepository.save(address);

        Address foundAddress = addressRepository.findById(savedAddress.getId()).orElse(null);

        assertNotNull(foundAddress);
        assertEquals("New York", foundAddress.getCity());
    }

    @Test
    void findByUserId_whenRecordsExist_returnsUserRecords() {
        User user2 = new User();
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setEmail("jane@example.com");
        user2.setPasswordHash("hashedPassword");
        userRepository.save(user2);

        Address address1 = new Address();
        address1.setCountry("USA");
        address1.setRegion("TX");
        address1.setCity("Houston");
        address1.setPostalCode(77001);
        address1.setAddressLine("789 Road");
        address1.setUser(user);
        addressRepository.save(address1);

        Address address2 = new Address();
        address2.setCountry("USA");
        address2.setRegion("TX");
        address2.setCity("Dallas");
        address2.setPostalCode(75201);
        address2.setAddressLine("321 Ave");
        address2.setUser(user);
        addressRepository.save(address2);

        Address address3 = new Address();
        address3.setCountry("Canada");
        address3.setRegion("ON");
        address3.setCity("Toronto");
        address3.setPostalCode(10001);
        address3.setAddressLine("555 Street");
        address3.setUser(user2);
        addressRepository.save(address3);

        List<Address> userAddresses = addressRepository.findByUserId(user.getId());

        assertEquals(2, userAddresses.size());
        assertTrue(userAddresses.stream().allMatch(a -> a.getUser().getId().equals(user.getId())));
    }

    @Test
    void findByUserId_whenRecordsAreMissing_returnsEmptyList() {
        User user3 = new User();
        user3.setFirstName("Bob");
        user3.setLastName("Brown");
        user3.setEmail("bob@example.com");
        user3.setPasswordHash("hashedPassword");
        userRepository.save(user3);

        List<Address> addresses = addressRepository.findByUserId(user3.getId());

        assertEquals(0, addresses.size());
    }

    @Test
    void deleteById_whenAddressExists_removesAddress() {
        Address address = new Address();
        address.setCountry("USA");
        address.setRegion("FL");
        address.setCity("Miami");
        address.setPostalCode(33101);
        address.setAddressLine("999 Beach");
        address.setUser(user);
        Address savedAddress = addressRepository.save(address);

        addressRepository.deleteById(savedAddress.getId());

        assertFalse(addressRepository.existsById(savedAddress.getId()));
    }

    @Test
    void deleteByUserId_whenUserHasRecords_removesUserRecords() {
        Address address = new Address();
        address.setCountry("USA");
        address.setRegion("FL");
        address.setCity("Miami");
        address.setPostalCode(33101);
        address.setAddressLine("999 Beach");
        address.setUser(user);
        addressRepository.save(address);

        addressRepository.deleteByUserId(user.getId());

        assertTrue(addressRepository.findByUserId(user.getId()).isEmpty());
    }

    @Test
    void count_whenAddressesExist_returnsAddressCount() {
        Address address1 = new Address();
        address1.setCountry("USA");
        address1.setRegion("WA");
        address1.setCity("Seattle");
        address1.setPostalCode(98101);
        address1.setAddressLine("111 Pine");
        address1.setUser(user);

        Address address2 = new Address();
        address2.setCountry("USA");
        address2.setRegion("OR");
        address2.setCity("Portland");
        address2.setPostalCode(97204);
        address2.setAddressLine("222 Oak");
        address2.setUser(user);

        addressRepository.save(address1);
        addressRepository.save(address2);

        long count = addressRepository.count();
        assertTrue(count >= 2);
    }
}




