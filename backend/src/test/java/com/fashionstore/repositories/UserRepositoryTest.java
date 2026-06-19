package com.fashionstore.repositories;

import com.fashionstore.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveUser() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setPasswordHash("hashedPassword");

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("John", savedUser.getFirstName());
        assertEquals("john@example.com", savedUser.getEmail());
    }

    @Test
    void testFindUserById() {
        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setEmail("jane@example.com");
        user.setPasswordHash("hashedPassword");
        User savedUser = userRepository.save(user);

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals("Jane", foundUser.get().getFirstName());
    }

    @Test
    void testFindByEmail() {
        User user = new User();
        user.setFirstName("Bob");
        user.setLastName("Johnson");
        user.setEmail("bob@example.com");
        user.setPasswordHash("hashedPassword");
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail("bob@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("Bob", foundUser.get().getFirstName());
    }

    @Test
    void testFindByEmailNotFound() {
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(foundUser.isPresent());
    }

    @Test
    void testDeleteUser() {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("Delete");
        user.setEmail("delete@example.com");
        user.setPasswordHash("hashedPassword");
        User savedUser = userRepository.save(user);

        userRepository.deleteById(savedUser.getId());

        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void testCountUsers() {
        User user1 = new User();
        user1.setFirstName("User1");
        user1.setLastName("Last1");
        user1.setEmail("user1@example.com");
        user1.setPasswordHash("hashedPassword");

        User user2 = new User();
        user2.setFirstName("User2");
        user2.setLastName("Last2");
        user2.setEmail("user2@example.com");
        user2.setPasswordHash("hashedPassword");

        userRepository.save(user1);
        userRepository.save(user2);

        long count = userRepository.count();
        assertTrue(count >= 2);
    }

    @Test
    void testExistsByIdTrue() {
        User user = new User();
        user.setFirstName("Exists");
        user.setLastName("Test");
        user.setEmail("exists@example.com");
        user.setPasswordHash("hashedPassword");
        User savedUser = userRepository.save(user);

        boolean exists = userRepository.existsById(savedUser.getId());
        assertTrue(exists);
    }

    @Test
    void testExistsByIdFalse() {
        boolean exists = userRepository.existsById(99999L);
        assertFalse(exists);
    }
}






