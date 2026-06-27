package com.fashionstore.controllers;

import com.fashionstore.services.CategoryService;
import com.fashionstore.controllers.admin.AdminCategoryController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest extends ControllerTestSupport {
    @Mock private CategoryService categoryServiceMock;
    private AdminCategoryController objectUnderTest;

    @BeforeEach
    void setUp() {
        objectUnderTest = new AdminCategoryController(categoryServiceMock);
    }

    @Test
    void createCategory_whenRequestIsValid_returnsCreatedCategory() {
        when(categoryServiceMock.createCategory(any())).thenReturn(categoryResponse(1L, "Shoes", null));

        var response = objectUnderTest.createCategory(categoryRequest("Shoes", null));

        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Category creation should return HTTP 201");
        assertEquals("Shoes", response.getBody().getName(), "Created category name should match service response");
    }

    @Test
    void updateCategory_whenCategoryExists_returnsUpdatedCategory() {
        when(categoryServiceMock.updateCategory(org.mockito.ArgumentMatchers.eq(1L), any()))
                .thenReturn(categoryResponse(1L, "Sneakers", null));

        var response = objectUnderTest.updateCategory(1L, categoryRequest("Sneakers", null));

        assertEquals("Sneakers", response.getBody().getName(), "Updated category name should match service response");
    }

    @Test
    void getCategoryById_whenCategoryExists_returnsCategory() {
        when(categoryServiceMock.getCategoryById(1L)).thenReturn(categoryResponse(1L, "Sneakers", null));

        var response = objectUnderTest.getCategoryById(1L);

        assertEquals(1L, response.getBody().getId(), "Fetched category id should match requested category");
    }

    @Test
    void getPagedCategories_whenPageIsRequested_returnsPagedCategories() {
        when(categoryServiceMock.getPagedAdminCategories(1, 20, null, null, null)).thenReturn(pageResponse(adminCategoryResponse(1L, "Sneakers", null, null)));

        var response = objectUnderTest.getPagedCategories(1, 20, null, null, null);

        assertEquals(1, response.getBody().getContent().size(), "Category page should contain service results");
    }

    @Test
    void deleteCategory_whenCategoryExists_returnsNoContent() {
        var response = objectUnderTest.deleteCategory(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "Category deletion should return HTTP 204");
        assertNull(response.getBody(), "Delete response body should be empty");
        verify(categoryServiceMock).deleteCategory(1L);
    }
}
