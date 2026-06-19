package com.fashionstore.repositories;

import com.fashionstore.models.Category;
import com.fashionstore.models.Color;
import com.fashionstore.models.Item;
import com.fashionstore.models.ItemVariant;
import com.fashionstore.models.Size;
import com.fashionstore.vo.Audience;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemVariantRepositoryTest {

    @Autowired
    private ItemVariantRepository itemVariantRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Item item;
    private Color color;
    private Size size;

    @BeforeEach
    void setUp() {
        Category category = new Category();
        category.setName("Test Category");
        categoryRepository.save(category);

        item = new Item();
        item.setName("Test Item");
        item.setPrice(99.99f);
        item.setAudience(Audience.UNISEX);
        item.setCategory(category);
        itemRepository.save(item);

        color = new Color();
        color.setName("Red");
        colorRepository.save(color);

        size = new Size();
        sizeRepository.save(size);
    }

    @Test
    void testSaveItemVariant() {
        ItemVariant variant = new ItemVariant();
        variant.setItem(item);
        variant.setColor(color);
        variant.setSize(size);
        variant.setActive(true);
        variant.setStockLeft(10);

        ItemVariant savedVariant = itemVariantRepository.save(variant);

        assertNotNull(savedVariant.getId());
        assertTrue(savedVariant.isActive());
        assertEquals(10, savedVariant.getStockLeft());
    }

    @Test
    void testFindItemVariantById() {
        ItemVariant variant = new ItemVariant();
        variant.setItem(item);
        variant.setColor(color);
        variant.setSize(size);
        variant.setActive(true);
        variant.setStockLeft(5);
        ItemVariant savedVariant = itemVariantRepository.save(variant);

        ItemVariant foundVariant = itemVariantRepository.findById(savedVariant.getId()).orElse(null);

        assertNotNull(foundVariant);
        assertTrue(foundVariant.isActive());
    }

    @Test
    void testFindByItemIdAndIsActiveTrue() {
        ItemVariant activeVariant = new ItemVariant();
        activeVariant.setItem(item);
        activeVariant.setColor(color);
        activeVariant.setSize(size);
        activeVariant.setActive(true);
        activeVariant.setStockLeft(10);
        itemVariantRepository.save(activeVariant);

        ItemVariant inactiveVariant = new ItemVariant();
        inactiveVariant.setItem(item);
        inactiveVariant.setColor(color);
        inactiveVariant.setSize(size);
        inactiveVariant.setActive(false);
        inactiveVariant.setStockLeft(0);
        itemVariantRepository.save(inactiveVariant);

        List<ItemVariant> activeVariants = itemVariantRepository.findByItemIdAndIsActiveTrue(item.getId());

        assertEquals(1, activeVariants.size());
        assertTrue(activeVariants.get(0).isActive());
    }

    @Test
    void testFindByIdAndIsActiveTrue() {
        ItemVariant activeVariant = new ItemVariant();
        activeVariant.setItem(item);
        activeVariant.setColor(color);
        activeVariant.setSize(size);
        activeVariant.setActive(true);
        activeVariant.setStockLeft(10);
        ItemVariant savedVariant = itemVariantRepository.save(activeVariant);

        Optional<ItemVariant> foundVariant = itemVariantRepository.findByIdAndIsActiveTrue(savedVariant.getId());

        assertTrue(foundVariant.isPresent());
        assertTrue(foundVariant.get().isActive());
    }

    @Test
    void testFindByIdAndIsActiveTrueInactive() {
        ItemVariant inactiveVariant = new ItemVariant();
        inactiveVariant.setItem(item);
        inactiveVariant.setColor(color);
        inactiveVariant.setSize(size);
        inactiveVariant.setActive(false);
        inactiveVariant.setStockLeft(0);
        ItemVariant savedVariant = itemVariantRepository.save(inactiveVariant);

        Optional<ItemVariant> foundVariant = itemVariantRepository.findByIdAndIsActiveTrue(savedVariant.getId());

        assertFalse(foundVariant.isPresent());
    }

    @Test
    void testDeleteItemVariant() {
        ItemVariant variant = new ItemVariant();
        variant.setItem(item);
        variant.setColor(color);
        variant.setSize(size);
        variant.setActive(true);
        variant.setStockLeft(5);
        ItemVariant savedVariant = itemVariantRepository.save(variant);

        itemVariantRepository.deleteById(savedVariant.getId());

        assertFalse(itemVariantRepository.existsById(savedVariant.getId()));
    }

    @Test
    void testCountItemVariants() {
        ItemVariant variant1 = new ItemVariant();
        variant1.setItem(item);
        variant1.setColor(color);
        variant1.setSize(size);
        variant1.setActive(true);
        variant1.setStockLeft(5);

        ItemVariant variant2 = new ItemVariant();
        variant2.setItem(item);
        variant2.setColor(color);
        variant2.setSize(size);
        variant2.setActive(true);
        variant2.setStockLeft(10);

        itemVariantRepository.save(variant1);
        itemVariantRepository.save(variant2);

        long count = itemVariantRepository.count();
        assertTrue(count >= 2);
    }
}





