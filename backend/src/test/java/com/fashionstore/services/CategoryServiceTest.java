package com.fashionstore.services;

import com.fashionstore.dto.response.CategoryResponse;
import com.fashionstore.exceptions.ConflictException;
import com.fashionstore.repositories.CategoryRepository;
import com.fashionstore.repositories.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest extends ServiceTestSupport {
    @Mock private CategoryRepository categoryRepository;
    @Mock private ItemRepository itemRepository;
    @InjectMocks private CategoryService categoryService;

    @Test
    void createsCategoryWithParent() {
        var parent = category(1L, "Parent");
        var child = category(2L, "Child");
        child.setParent(parent);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(categoryRepository.save(any())).thenReturn(child);

        CategoryResponse created = categoryService.createCategory(categoryRequest("Child", 1L));

        assertEquals(2L, created.getId());
        assertEquals(1L, created.getParentId());
    }

    @Test
    void updatesCategory() {
        var existing = category(1L, "Old");
        var updated = category(1L, "Updated");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(updated);

        assertEquals("Updated", categoryService.updateCategory(1L, categoryRequest("Updated", null)).getName());
    }

    @Test
    void blocksDeletingCategoryUsedByItems() {
        when(categoryRepository.existsByParentId(1L)).thenReturn(false);
        when(itemRepository.existsByCategoryId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> categoryService.deleteCategory(1L));
        verify(categoryRepository, never()).deleteById(1L);
    }

    @Test
    void blocksDeletingCategoryWithSubcategories() {
        when(categoryRepository.existsByParentId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> categoryService.deleteCategory(1L));
        verify(categoryRepository, never()).deleteById(1L);
    }

    @Test
    void deletesUnusedCategory() {
        when(categoryRepository.existsByParentId(1L)).thenReturn(false);
        when(itemRepository.existsByCategoryId(1L)).thenReturn(false);

        categoryService.deleteCategory(1L);

        verify(categoryRepository).deleteById(1L);
    }
}
