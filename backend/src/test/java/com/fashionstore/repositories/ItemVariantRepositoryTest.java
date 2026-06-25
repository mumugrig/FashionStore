package com.fashionstore.repositories;

import com.fashionstore.models.Category;
import com.fashionstore.models.Color;
import com.fashionstore.models.Item;
import com.fashionstore.models.ItemVariant;
import com.fashionstore.models.Size;
import com.fashionstore.vo.Audience;
import com.fashionstore.vo.SizeSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
        item.setPrice(BigDecimal.valueOf(99.99f));
        item.setDescription("Variant item description");
        item.setAudience(Audience.UNISEX);
        item.setCategory(category);
        itemRepository.save(item);

        color = new Color();
        color.setName("Red");
        color.setValue("#ff0000");
        colorRepository.save(color);

        size = new Size();
        size.setLabel("M");
        size.setSizeSystem(SizeSystem.ALPHA);
        sizeRepository.save(size);
    }

    @Test
    void save_whenItemVariantIsValid_persistsItemVariant() {
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
    void findById_whenItemVariantExists_returnsItemVariant() {
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
    void findByItemIdAndIsActiveTrue_whenActiveVariantsExist_returnsActiveVariants() {
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
    void findByItemIdAndExistsBySizeAndColor_whenVariantExists_returnsVariantAndExistenceFlags() {
        ItemVariant variant = new ItemVariant();
        variant.setItem(item);
        variant.setColor(color);
        variant.setSize(size);
        variant.setActive(true);
        variant.setStockLeft(10);
        itemVariantRepository.save(variant);

        List<ItemVariant> variants = itemVariantRepository.findByItemId(item.getId());

        assertEquals(1, variants.size());
        assertTrue(itemVariantRepository.existsBySizeId(size.getId()));
        assertTrue(itemVariantRepository.existsByColorId(color.getId()));
    }

    @Test
    void findByIdAndIsActiveTrue_whenVariantIsActive_returnsVariant() {
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
    void findByIdAndIsActiveTrue_whenVariantIsInactive_returnsEmpty() {
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
    void deleteById_whenItemVariantExists_removesItemVariant() {
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
    void count_whenItemVariantsExist_returnsItemVariantCount() {
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





