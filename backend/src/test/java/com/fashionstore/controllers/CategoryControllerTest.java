package com.fashionstore.controllers;

import com.fashionstore.services.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest extends ControllerTestSupport {
    @Mock private CategoryService categoryService;
    @InjectMocks private CategoryController categoryController;

    @Test
    void createsUpdatesReadsListsAndDeletesCategories() {
        when(categoryService.createCategory(any())).thenReturn(categoryResponse(1L, "Shoes", null));
        when(categoryService.updateCategory(org.mockito.ArgumentMatchers.eq(1L), any())).thenReturn(categoryResponse(1L, "Sneakers", null));
        when(categoryService.getCategoryById(1L)).thenReturn(categoryResponse(1L, "Sneakers", null));
        when(categoryService.getAllCategories()).thenReturn(List.of(categoryResponse(1L, "Sneakers", null)));

        var created = categoryController.createCategory(categoryRequest("Shoes", null));
        var updated = categoryController.updateCategory(1L, categoryRequest("Sneakers", null));
        var fetched = categoryController.getCategoryById(1L);
        var listed = categoryController.getAllCategories();
        var deleted = categoryController.deleteCategory(1L);

        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        assertEquals("Sneakers", updated.getBody().getName());
        assertEquals(1L, fetched.getBody().getId());
        assertEquals(1, listed.getBody().size());
        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());
        assertNull(deleted.getBody());
        verify(categoryService).deleteCategory(1L);
    }
}
