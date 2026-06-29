package com.fashionstore.repositories;

import com.fashionstore.models.Category;
import com.fashionstore.models.Color;
import com.fashionstore.models.Favorite;
import com.fashionstore.models.Item;
import com.fashionstore.models.ItemVariant;
import com.fashionstore.models.Size;
import com.fashionstore.models.User;
import com.fashionstore.vo.Audience;
import com.fashionstore.vo.SizeSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FavoriteRepositoryTest {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemVariantRepository itemVariantRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private SizeRepository sizeRepository;

    private User user;
    private ItemVariant itemVariant;
    private Item item;
    private Size size;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFirstName("Favorite");
        user.setLastName("Lover");
        user.setEmail("favorite@example.com");
        user.setPasswordHash("hashedPassword");
        userRepository.save(user);

        Category category = new Category();
        category.setName("Favorites");
        categoryRepository.save(category);

        item = new Item();
        item.setName("Favorite Item");
        item.setPrice(BigDecimal.valueOf(99.99f));
        item.setDescription("Favorite item description");
        item.setAudience(Audience.UNISEX);
        item.setCategory(category);
        itemRepository.save(item);

        Color color = new Color();
        color.setName("White");
        color.setValue("#ffffff");
        colorRepository.save(color);

        size = new Size();
        size.setLabel("M");
        size.setSizeSystem(SizeSystem.ALPHA);
        sizeRepository.save(size);

        itemVariant = new ItemVariant();
        itemVariant.setItem(item);
        itemVariant.setColor(color);
        itemVariant.setSize(size);
        itemVariant.setActive(true);
        itemVariant.setStockLeft(100);
        itemVariantRepository.save(itemVariant);
    }

    @Test
    void save_whenFavoriteIsValid_persistsFavorite() {
        Favorite favorite = new Favorite();
        favorite.setItemVariant(itemVariant);
        favorite.setUser(user);

        Favorite savedFavorite = favoriteRepository.save(favorite);

        assertNotNull(savedFavorite.getId());
        assertEquals(user.getId(), savedFavorite.getUser().getId());
    }

    @Test
    void findById_whenFavoriteExists_returnsFavorite() {
        Favorite favorite = new Favorite();
        favorite.setItemVariant(itemVariant);
        favorite.setUser(user);
        Favorite savedFavorite = favoriteRepository.save(favorite);

        Favorite foundFavorite = favoriteRepository.findById(savedFavorite.getId()).orElse(null);

        assertNotNull(foundFavorite);
        assertEquals(itemVariant.getId(), foundFavorite.getItemVariant().getId());
    }

    @Test
    void findByUserId_whenRecordsExist_returnsUserRecords() {
        User user2 = new User();
        user2.setFirstName("Another");
        user2.setLastName("Person");
        user2.setEmail("another@example.com");
        user2.setPasswordHash("hashedPassword");
        userRepository.save(user2);

        Favorite favorite1 = new Favorite();
        favorite1.setItemVariant(itemVariant);
        favorite1.setUser(user);
        favoriteRepository.save(favorite1);

        Favorite favorite2 = new Favorite();
        favorite2.setItemVariant(createItemVariant("Cream", "#fffdd0"));
        favorite2.setUser(user);
        favoriteRepository.save(favorite2);

        Favorite favorite3 = new Favorite();
        favorite3.setItemVariant(itemVariant);
        favorite3.setUser(user2);
        favoriteRepository.save(favorite3);

        List<Favorite> userFavorites = favoriteRepository.findByUserId(user.getId());

        assertEquals(2, userFavorites.size());
        assertEquals(2, favoriteRepository.findByUserId(user.getId(), PageRequest.of(0, 20)).getTotalElements());
        assertTrue(userFavorites.stream().allMatch(f -> f.getUser().getId().equals(user.getId())));
        assertTrue(favoriteRepository.existsByItemVariantId(itemVariant.getId()));
        assertTrue(favoriteRepository.existsByItemVariantItemId(itemVariant.getItem().getId()));
    }

    @Test
    void findByUserId_whenRecordsAreMissing_returnsEmptyList() {
        User emptyUser = new User();
        emptyUser.setFirstName("No");
        emptyUser.setLastName("Favorites");
        emptyUser.setEmail("nofav@example.com");
        emptyUser.setPasswordHash("hashedPassword");
        userRepository.save(emptyUser);

        List<Favorite> favorites = favoriteRepository.findByUserId(emptyUser.getId());

        assertEquals(0, favorites.size());
    }

    @Test
    void deleteById_whenFavoriteExists_removesFavorite() {
        Favorite favorite = new Favorite();
        favorite.setItemVariant(itemVariant);
        favorite.setUser(user);
        Favorite savedFavorite = favoriteRepository.save(favorite);

        favoriteRepository.deleteById(savedFavorite.getId());

        assertFalse(favoriteRepository.existsById(savedFavorite.getId()));
    }

    @Test
    void deleteByUserId_whenUserHasRecords_removesUserRecords() {
        Favorite favorite = new Favorite();
        favorite.setItemVariant(itemVariant);
        favorite.setUser(user);
        favoriteRepository.save(favorite);

        favoriteRepository.deleteByUserId(user.getId());

        assertTrue(favoriteRepository.findByUserId(user.getId()).isEmpty());
    }

    @Test
    void count_whenFavoritesExist_returnsFavoriteCount() {
        Favorite favorite1 = new Favorite();
        favorite1.setItemVariant(itemVariant);
        favorite1.setUser(user);

        Favorite favorite2 = new Favorite();
        favorite2.setItemVariant(createItemVariant("Cream", "#fffdd0"));
        favorite2.setUser(user);

        favoriteRepository.save(favorite1);
        favoriteRepository.save(favorite2);

        long count = favoriteRepository.count();
        assertTrue(count >= 2);
    }

    private ItemVariant createItemVariant(String colorName, String colorValue) {
        Color color = new Color();
        color.setName(colorName);
        color.setValue(colorValue);
        colorRepository.save(color);

        ItemVariant variant = new ItemVariant();
        variant.setItem(item);
        variant.setColor(color);
        variant.setSize(size);
        variant.setActive(true);
        variant.setStockLeft(100);
        return itemVariantRepository.save(variant);
    }
}





