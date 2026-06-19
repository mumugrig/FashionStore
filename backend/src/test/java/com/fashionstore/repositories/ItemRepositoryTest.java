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
    void testSaveItem() {
        Item item = new Item();
        item.setName("Laptop");
        item.setPrice(999.99f);
        item.setDescription("High performance laptop");
        item.setAudience(Audience.UNISEX);
        item.setCategory(category);

        Item savedItem = itemRepository.save(item);

        assertNotNull(savedItem.getId());
        assertEquals("Laptop", savedItem.getName());
        assertEquals(999.99f, savedItem.getPrice());
    }

    @Test
    void testFindItemById() {
        Item item = new Item();
        item.setName("Mouse");
        item.setPrice(29.99f);
        item.setDescription("Wireless mouse");
        item.setAudience(Audience.UNISEX);
        item.setCategory(category);
        Item savedItem = itemRepository.save(item);

        Item foundItem = itemRepository.findById(savedItem.getId()).orElse(null);

        assertNotNull(foundItem);
        assertEquals("Mouse", foundItem.getName());
    }

    @Test
    void testFindByNameContainingIgnoreCase() {
        Item item1 = new Item();
        item1.setName("Samsung Laptop");
        item1.setPrice(899.99f);
        item1.setAudience(Audience.UNISEX);
        item1.setCategory(category);

        Item item2 = new Item();
        item2.setName("LAPTOP Stand");
        item2.setPrice(49.99f);
        item2.setAudience(Audience.UNISEX);
        item2.setCategory(category);

        Item item3 = new Item();
        item3.setName("USB Cable");
        item3.setPrice(9.99f);
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
    void testFindByCategoryId() {
        Category category2 = new Category();
        category2.setName("Accessories");
        categoryRepository.save(category2);

        Item item1 = new Item();
        item1.setName("Item1");
        item1.setPrice(19.99f);
        item1.setAudience(Audience.UNISEX);
        item1.setCategory(category);

        Item item2 = new Item();
        item2.setName("Item2");
        item2.setPrice(29.99f);
        item2.setAudience(Audience.UNISEX);
        item2.setCategory(category);

        Item item3 = new Item();
        item3.setName("Item3");
        item3.setPrice(39.99f);
        item3.setAudience(Audience.UNISEX);
        item3.setCategory(category2);

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        List<Item> itemsInCategory = itemRepository.findByCategoryId(category.getId());

        assertEquals(2, itemsInCategory.size());
    }

    @Test
    void testDeleteItem() {
        Item item = new Item();
        item.setName("ToDelete");
        item.setPrice(9.99f);
        item.setAudience(Audience.UNISEX);
        item.setCategory(category);
        Item savedItem = itemRepository.save(item);

        itemRepository.deleteById(savedItem.getId());

        assertFalse(itemRepository.existsById(savedItem.getId()));
    }

    @Test
    void testCountItems() {
        Item item1 = new Item();
        item1.setName("Item1");
        item1.setPrice(19.99f);
        item1.setAudience(Audience.UNISEX);
        item1.setCategory(category);

        Item item2 = new Item();
        item2.setName("Item2");
        item2.setPrice(29.99f);
        item2.setAudience(Audience.UNISEX);
        item2.setCategory(category);

        itemRepository.save(item1);
        itemRepository.save(item2);

        long count = itemRepository.count();
        assertTrue(count >= 2);
    }
}




