package com.fashionstore.repositories;

import com.fashionstore.models.Category;
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
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void testSaveCategory() {
        Category category = new Category();
        category.setName("Clothing");

        Category savedCategory = categoryRepository.save(category);

        assertNotNull(savedCategory.getId());
        assertEquals("Clothing", savedCategory.getName());
    }

    @Test
    void testFindCategoryById() {
        Category category = new Category();
        category.setName("Shoes");
        Category savedCategory = categoryRepository.save(category);

        Category foundCategory = categoryRepository.findById(savedCategory.getId()).orElse(null);

        assertNotNull(foundCategory);
        assertEquals("Shoes", foundCategory.getName());
    }

    @Test
    void testFindByParentId() {
        Category parent = new Category();
        parent.setName("Fashion");
        Category savedParent = categoryRepository.save(parent);

        Category child1 = new Category();
        child1.setName("Men Clothing");
        child1.setParent(savedParent);
        categoryRepository.save(child1);

        Category child2 = new Category();
        child2.setName("Women Clothing");
        child2.setParent(savedParent);
        categoryRepository.save(child2);

        Category unrelated = new Category();
        unrelated.setName("Electronics");
        categoryRepository.save(unrelated);

        List<Category> children = categoryRepository.findByParentId(savedParent.getId());

        assertEquals(2, children.size());
        assertTrue(children.stream().allMatch(c -> c.getParent().getId().equals(savedParent.getId())));
    }

    @Test
    void testFindByParentIdEmpty() {
        Category orphanCategory = new Category();
        orphanCategory.setName("NoChildren");
        categoryRepository.save(orphanCategory);

        List<Category> children = categoryRepository.findByParentId(orphanCategory.getId());

        assertEquals(0, children.size());
    }

    @Test
    void testDeleteCategory() {
        Category category = new Category();
        category.setName("ToDelete");
        Category savedCategory = categoryRepository.save(category);

        categoryRepository.deleteById(savedCategory.getId());

        assertFalse(categoryRepository.existsById(savedCategory.getId()));
    }

    @Test
    void testCountCategories() {
        Category cat1 = new Category();
        cat1.setName("Category1");
        categoryRepository.save(cat1);

        Category cat2 = new Category();
        cat2.setName("Category2");
        categoryRepository.save(cat2);

        long count = categoryRepository.count();
        assertTrue(count >= 2);
    }
}




