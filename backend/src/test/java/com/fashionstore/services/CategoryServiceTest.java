package com.fashionstore.services;

import com.fashionstore.dto.response.CategoryResponse;
import com.fashionstore.exceptions.ConflictException;
import com.fashionstore.repositories.CategoryRepository;
import com.fashionstore.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    @Mock private CategoryRepository categoryRepositoryMock;
    @Mock private ItemRepository itemRepositoryMock;
    private CategoryService objectUnderTest;

    @BeforeEach
    void setUp() {
        objectUnderTest = new CategoryService(categoryRepositoryMock, itemRepositoryMock);
    }

    @Test
    void createCategory_whenParentExists_returnsCreatedCategory() {
        var parent = category(1L, "Parent");
        var child = category(2L, "Child");
        child.setParent(parent);
        when(categoryRepositoryMock.findById(1L)).thenReturn(Optional.of(parent));
        when(categoryRepositoryMock.save(any())).thenReturn(child);

        CategoryResponse response = objectUnderTest.createCategory(categoryRequest("Child", 1L));

        assertEquals(2L, response.getId(), "Created category id should match saved entity");
        assertEquals(1L, response.getParentId(), "Created category should reference requested parent");
    }

    @Test
    void updateCategory_whenCategoryExists_returnsUpdatedCategory() {
        var existing = category(1L, "Old");
        var updated = category(1L, "Updated");
        when(categoryRepositoryMock.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepositoryMock.save(existing)).thenReturn(updated);

        CategoryResponse response = objectUnderTest.updateCategory(1L, categoryRequest("Updated", null));

        assertEquals("Updated", response.getName(), "Updated category name should match saved entity");
    }

    @Test
    void deleteCategory_whenCategoryIsUsedByItems_throwsConflictException() {
        when(categoryRepositoryMock.existsById(1L)).thenReturn(true);
        when(categoryRepositoryMock.existsByParentId(1L)).thenReturn(false);
        when(itemRepositoryMock.existsByCategoryId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> objectUnderTest.deleteCategory(1L));
        verify(categoryRepositoryMock, never()).deleteById(1L);
    }

    @Test
    void deleteCategory_whenCategoryHasSubcategories_throwsConflictException() {
        when(categoryRepositoryMock.existsById(1L)).thenReturn(true);
        when(categoryRepositoryMock.existsByParentId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> objectUnderTest.deleteCategory(1L));
        verify(categoryRepositoryMock, never()).deleteById(1L);
    }

    @Test
    void deleteCategory_whenCategoryIsUnused_deletesCategory() {
        when(categoryRepositoryMock.existsById(1L)).thenReturn(true);
        when(categoryRepositoryMock.existsByParentId(1L)).thenReturn(false);
        when(itemRepositoryMock.existsByCategoryId(1L)).thenReturn(false);

        objectUnderTest.deleteCategory(1L);

        verify(categoryRepositoryMock).deleteById(1L);
    }
}
