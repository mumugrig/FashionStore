package com.fashionstore.repositories;

import com.fashionstore.models.CartItem;
import com.fashionstore.models.Category;
import com.fashionstore.models.Color;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

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
        user.setFirstName("Cart");
        user.setLastName("Tester");
        user.setEmail("cart@example.com");
        user.setPasswordHash("hashedPassword");
        userRepository.save(user);

        Category category = new Category();
        category.setName("Products");
        categoryRepository.save(category);

        item = new Item();
        item.setName("Product");
        item.setPrice(BigDecimal.valueOf(29.99f));
        item.setDescription("Cart product description");
        item.setAudience(Audience.UNISEX);
        item.setCategory(category);
        itemRepository.save(item);

        Color color = new Color();
        color.setName("Black");
        color.setValue("#000000");
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
        itemVariant.setStockLeft(50);
        itemVariantRepository.save(itemVariant);
    }

    @Test
    void save_whenCartItemIsValid_persistsCartItem() {
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(2);
        cartItem.setItemVariant(itemVariant);
        cartItem.setUser(user);

        CartItem savedCartItem = cartItemRepository.save(cartItem);

        assertNotNull(savedCartItem.getId());
        assertEquals(2, savedCartItem.getQuantity());
    }

    @Test
    void findById_whenCartItemExists_returnsCartItem() {
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(3);
        cartItem.setItemVariant(itemVariant);
        cartItem.setUser(user);
        CartItem savedCartItem = cartItemRepository.save(cartItem);

        CartItem foundCartItem = cartItemRepository.findById(savedCartItem.getId()).orElse(null);

        assertNotNull(foundCartItem);
        assertEquals(3, foundCartItem.getQuantity());
    }

    @Test
    void findByUserId_whenRecordsExist_returnsUserRecords() {
        User user2 = new User();
        user2.setFirstName("Another");
        user2.setLastName("User");
        user2.setEmail("another@example.com");
        user2.setPasswordHash("hashedPassword");
        userRepository.save(user2);

        CartItem cartItem1 = new CartItem();
        cartItem1.setQuantity(1);
        cartItem1.setItemVariant(itemVariant);
        cartItem1.setUser(user);
        cartItemRepository.save(cartItem1);

        CartItem cartItem2 = new CartItem();
        cartItem2.setQuantity(2);
        cartItem2.setItemVariant(createItemVariant("Navy", "#000080"));
        cartItem2.setUser(user);
        cartItemRepository.save(cartItem2);

        CartItem cartItem3 = new CartItem();
        cartItem3.setQuantity(1);
        cartItem3.setItemVariant(itemVariant);
        cartItem3.setUser(user2);
        cartItemRepository.save(cartItem3);

        List<CartItem> userCartItems = cartItemRepository.findByUserId(user.getId());

        assertEquals(2, userCartItems.size());
        assertEquals(2, cartItemRepository.findByUserId(user.getId(), PageRequest.of(0, 20)).getTotalElements());
        assertTrue(userCartItems.stream().allMatch(c -> c.getUser().getId().equals(user.getId())));
        assertTrue(cartItemRepository.existsByItemVariantId(itemVariant.getId()));
        assertTrue(cartItemRepository.existsByItemVariantItemId(itemVariant.getItem().getId()));
    }

    @Test
    void findByUserId_whenRecordsAreMissing_returnsEmptyList() {
        User emptyUser = new User();
        emptyUser.setFirstName("Empty");
        emptyUser.setLastName("Cart");
        emptyUser.setEmail("empty@example.com");
        emptyUser.setPasswordHash("hashedPassword");
        userRepository.save(emptyUser);

        List<CartItem> cartItems = cartItemRepository.findByUserId(emptyUser.getId());

        assertEquals(0, cartItems.size());
    }

    @Test
    void deleteById_whenCartItemExists_removesCartItem() {
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(1);
        cartItem.setItemVariant(itemVariant);
        cartItem.setUser(user);
        CartItem savedCartItem = cartItemRepository.save(cartItem);

        cartItemRepository.deleteById(savedCartItem.getId());

        assertFalse(cartItemRepository.existsById(savedCartItem.getId()));
    }

    @Test
    void deleteByUserId_whenUserHasRecords_removesUserRecords() {
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(1);
        cartItem.setItemVariant(itemVariant);
        cartItem.setUser(user);
        cartItemRepository.save(cartItem);

        cartItemRepository.deleteByUserId(user.getId());

        assertTrue(cartItemRepository.findByUserId(user.getId()).isEmpty());
    }

    @Test
    void count_whenCartItemsExist_returnsCartItemCount() {
        CartItem cartItem1 = new CartItem();
        cartItem1.setQuantity(1);
        cartItem1.setItemVariant(itemVariant);
        cartItem1.setUser(user);

        CartItem cartItem2 = new CartItem();
        cartItem2.setQuantity(2);
        cartItem2.setItemVariant(createItemVariant("Navy", "#000080"));
        cartItem2.setUser(user);

        cartItemRepository.save(cartItem1);
        cartItemRepository.save(cartItem2);

        long count = cartItemRepository.count();
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
        variant.setStockLeft(50);
        return itemVariantRepository.save(variant);
    }
}



