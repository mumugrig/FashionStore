package com.fashionstore.repositories;

import com.fashionstore.models.Category;
import com.fashionstore.models.Item;
import com.fashionstore.vo.Audience;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setName("Electronics");
        categoryRepository.save(category);
    }

    @Test
    void save_whenItemIsValid_persistsItem() {
        Item item = new Item();
        item.setName("Laptop");
        item.setPrice(BigDecimal.valueOf(999.99f));
        item.setDescription("High performance laptop");
        item.setAudience(Audience.UNISEX);
        item.setCategory(category);

        Item savedItem = itemRepository.save(item);

        assertNotNull(savedItem.getId());
        assertEquals("Laptop", savedItem.getName());
        assertEquals(BigDecimal.valueOf(999.99f), savedItem.getPrice());
    }

    @Test
    void findById_whenItemExists_returnsItem() {
        Item item = new Item();
        item.setName("Mouse");
        item.setPrice(BigDecimal.valueOf(29.99f));
        item.setDescription("Wireless mouse");
        item.setAudience(Audience.UNISEX);
        item.setCategory(category);
        Item savedItem = itemRepository.save(item);

        Item foundItem = itemRepository.findById(savedItem.getId()).orElse(null);

        assertNotNull(foundItem);
        assertEquals("Mouse", foundItem.getName());
    }

    @Test
    void findByNameContainingIgnoreCase_whenNameMatches_returnsItems() {
        Item item1 = new Item();
        item1.setName("Samsung Laptop");
        item1.setPrice(BigDecimal.valueOf(899.99f));
        item1.setDescription("Samsung laptop description");
        item1.setAudience(Audience.UNISEX);
        item1.setCategory(category);

        Item item2 = new Item();
        item2.setName("LAPTOP Stand");
        item2.setPrice(BigDecimal.valueOf(49.99f));
        item2.setDescription("Laptop stand description");
        item2.setAudience(Audience.UNISEX);
        item2.setCategory(category);

        Item item3 = new Item();
        item3.setName("USB Cable");
        item3.setPrice(BigDecimal.valueOf(9.99f));
        item3.setDescription("USB cable description");
        item3.setAudience(Audience.UNISEX);
        item3.setCategory(category);

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        List<Item> results = itemRepository.findByNameContainingIgnoreCase("laptop");

        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(i -> i.getName().toLowerCase().contains("laptop")));
    }

    @Test
    void findByCategoryId_whenItemsExist_returnsCategoryItems() {
        Category category2 = new Category();
        category2.setName("Accessories");
        categoryRepository.save(category2);

        Item item1 = new Item();
        item1.setName("Item1");
        item1.setPrice(BigDecimal.valueOf(19.99f));
        item1.setDescription("First category item");
        item1.setAudience(Audience.UNISEX);
        item1.setCategory(category);

        Item item2 = new Item();
        item2.setName("Item2");
        item2.setPrice(BigDecimal.valueOf(29.99f));
        item2.setDescription("Second category item");
        item2.setAudience(Audience.UNISEX);
        item2.setCategory(category);

        Item item3 = new Item();
        item3.setName("Item3");
        item3.setPrice(BigDecimal.valueOf(39.99f));
        item3.setDescription("Third category item");
        item3.setAudience(Audience.UNISEX);
        item3.setCategory(category2);

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        List<Item> itemsInCategory = itemRepository.findByCategoryId(category.getId());

        assertEquals(2, itemsInCategory.size());
    }

    @Test
    void deleteById_whenItemExists_removesItem() {
        Item item = new Item();
        item.setName("ToDelete");
        item.setPrice(BigDecimal.valueOf(9.99f));
        item.setDescription("Item to delete");
        item.setAudience(Audience.UNISEX);
        item.setCategory(category);
        Item savedItem = itemRepository.save(item);

        itemRepository.deleteById(savedItem.getId());

        assertFalse(itemRepository.existsById(savedItem.getId()));
    }

    @Test
    void count_whenItemsExist_returnsItemCount() {
        Item item1 = new Item();
        item1.setName("Item1");
        item1.setPrice(BigDecimal.valueOf(19.99f));
        item1.setDescription("Count item one");
        item1.setAudience(Audience.UNISEX);
        item1.setCategory(category);

        Item item2 = new Item();
        item2.setName("Item2");
        item2.setPrice(BigDecimal.valueOf(29.99f));
        item2.setDescription("Count item two");
        item2.setAudience(Audience.UNISEX);
        item2.setCategory(category);

        itemRepository.save(item1);
        itemRepository.save(item2);

        long count = itemRepository.count();
        assertTrue(count >= 2);
    }
}




