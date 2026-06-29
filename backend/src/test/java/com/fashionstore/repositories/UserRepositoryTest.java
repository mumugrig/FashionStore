package com.fashionstore.repositories;

import com.fashionstore.models.CartItem;
import com.fashionstore.models.Category;
import com.fashionstore.models.Color;
import com.fashionstore.models.Favorite;
import com.fashionstore.models.Item;
import com.fashionstore.models.ItemVariant;
import com.fashionstore.models.RefreshToken;
import com.fashionstore.models.Review;
import com.fashionstore.models.Size;
import com.fashionstore.models.User;
import com.fashionstore.vo.Audience;
import com.fashionstore.vo.Comfort;
import com.fashionstore.vo.Quality;
import com.fashionstore.vo.SizeFit;
import com.fashionstore.vo.SizeSystem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ColorRepository colorRepository;
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemVariantRepository itemVariantRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private SizeRepository sizeRepository;

    @Test
    void save_whenUserIsValid_persistsUser() {
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
    void findById_whenUserExists_returnsUser() {
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
    void findByEmail_whenUserExists_returnsUser() {
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
    void findByEmail_whenUserIsMissing_returnsEmpty() {
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(foundUser.isPresent());
    }

    @Test
    void deleteById_whenUserExists_removesUser() {
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
    void delete_whenUserHasOwnedRecords_cascadesToOwnedRecords() {
        User user = userRepository.save(user("cascade-delete@example.com"));
        ItemVariant variant = itemVariantRepository.save(itemVariant());

        CartItem cartItem = new CartItem();
        cartItem.setUser(user);
        cartItem.setItemVariant(variant);
        cartItem.setQuantity(1);
        Long cartItemId = cartItemRepository.save(cartItem).getId();

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setItemVariant(variant);
        Long favoriteId = favoriteRepository.save(favorite).getId();

        Review review = new Review();
        review.setUser(user);
        review.setItem(variant.getItem());
        review.setBody("This is a valid cascade delete review body.");
        review.setSizeFit(SizeFit.TRUE_TO_SIZE);
        review.setQuality(Quality.EXCELLENT);
        review.setComfort(Comfort.VERY_COMFORTABLE);
        Long reviewId = reviewRepository.save(review).getId();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash("cascade-delete-refresh-token-hash");
        refreshToken.setExpiresAt(Instant.now().plusSeconds(3600));
        Long refreshTokenId = refreshTokenRepository.save(refreshToken).getId();

        Long userId = user.getId();
        entityManager.flush();
        entityManager.clear();

        User reloadedUser = userRepository.findById(userId).orElseThrow();
        userRepository.delete(reloadedUser);
        userRepository.flush();

        assertFalse(userRepository.existsById(userId));
        assertFalse(cartItemRepository.existsById(cartItemId));
        assertFalse(favoriteRepository.existsById(favoriteId));
        assertFalse(reviewRepository.existsById(reviewId));
        assertFalse(refreshTokenRepository.existsById(refreshTokenId));
    }

    @Test
    void count_whenUsersExist_returnsUserCount() {
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
    void existsById_whenUserExists_returnsTrue() {
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
    void existsById_whenUserIsMissing_returnsFalse() {
        boolean exists = userRepository.existsById(99999L);
        assertFalse(exists);
    }

    private User user(String email) {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail(email);
        user.setPhoneNumber("1234567890");
        user.setPasswordHash("hashedPassword");
        return user;
    }

    private ItemVariant itemVariant() {
        Category category = new Category();
        category.setName("Cascade Category");
        Category savedCategory = categoryRepository.save(category);

        Color color = new Color();
        color.setName("Cascade Black");
        color.setValue("#111111");
        color.setImageUrl("https://example.com/black.png");
        Color savedColor = colorRepository.save(color);

        Size size = new Size();
        size.setLabel("M");
        size.setSizeSystem(SizeSystem.ALPHA);
        Size savedSize = sizeRepository.save(size);

        Item item = new Item();
        item.setName("Cascade Item");
        item.setPrice(BigDecimal.valueOf(29.90));
        item.setDescription("A valid cascade item description");
        item.setAudience(Audience.UNISEX);
        item.setCategory(savedCategory);
        Item savedItem = itemRepository.save(item);

        ItemVariant variant = new ItemVariant();
        variant.setItem(savedItem);
        variant.setColor(savedColor);
        variant.setSize(savedSize);
        variant.setActive(true);
        variant.setStockLeft(5);
        return variant;
    }
}






