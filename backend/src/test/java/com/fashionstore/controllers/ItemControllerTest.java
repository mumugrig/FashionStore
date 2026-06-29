package com.fashionstore.controllers;

import com.fashionstore.services.ItemService;
import com.fashionstore.controllers.admin.AdminItemController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest extends ControllerTestSupport {
    @Mock private ItemService itemServiceMock;
    private AdminItemController objectUnderTest;

    @BeforeEach
    void setUp() {
        objectUnderTest = new AdminItemController(itemServiceMock);
    }

    @Test
    void createItem_whenRequestIsValid_returnsCreatedItem() {
        when(itemServiceMock.createItem(any())).thenReturn(itemResponse(1L, "Jacket", 1L));

        var response = objectUnderTest.createItem(itemRequest("Jacket", 1L));

        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Item creation should return HTTP 201");
        assertEquals("Jacket", response.getBody().getName(), "Created item name should match service response");
    }

    @Test
    void updateItem_whenItemExists_returnsUpdatedItem() {
        when(itemServiceMock.updateItem(org.mockito.ArgumentMatchers.eq(1L), any()))
                .thenReturn(itemResponse(1L, "Rain Jacket", 1L));

        var response = objectUnderTest.updateItem(1L, itemRequest("Rain Jacket", 1L));

        assertEquals("Rain Jacket", response.getBody().getName(), "Updated item name should match service response");
    }

    @Test
    void getItemById_whenItemExists_returnsItem() {
        when(itemServiceMock.getItemById(1L)).thenReturn(itemResponse(1L, "Rain Jacket", 1L));

        var response = objectUnderTest.getItemById(1L);

        assertEquals(1L, response.getBody().getId(), "Fetched item id should match requested item");
    }

    @Test
    void getPagedItems_whenPageIsRequested_returnsPagedItems() {
        when(itemServiceMock.getPagedAdminItems(1, 20, null, null, null)).thenReturn(pageResponse(adminItemResponse(1L, "Rain Jacket", 1L)));

        var response = objectUnderTest.getPagedItems(1, 20, null, null, null);

        assertEquals(1, response.getBody().getContent().size(), "Item page should contain service results");
        assertEquals("Outerwear", response.getBody().getContent().get(0).getCategoryName(), "Admin item response should include category name");
    }

    @Test
    void getPagedItems_whenCamelCasePriceFiltersProvided_passesResolvedPriceRangeToService() {
        var userController = new com.fashionstore.controllers.user.ItemController(itemServiceMock);
        when(itemServiceMock.getPagedItems(
                org.mockito.ArgumentMatchers.eq(1),
                org.mockito.ArgumentMatchers.eq(20),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.eq(BigDecimal.valueOf(10)),
                org.mockito.ArgumentMatchers.eq(BigDecimal.valueOf(50))))
                .thenReturn(pageResponse(itemResponse(1L, "Rain Jacket", 1L)));

        var response = userController.getPagedItems(
                1, 20, null, null, null, null, null,
                null, null, BigDecimal.valueOf(10), BigDecimal.valueOf(50));

        assertEquals(1, response.getBody().getContent().size(), "Price range query should return service results");
        verify(itemServiceMock).getPagedItems(
                org.mockito.ArgumentMatchers.eq(1),
                org.mockito.ArgumentMatchers.eq(20),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.eq(BigDecimal.valueOf(10)),
                org.mockito.ArgumentMatchers.eq(BigDecimal.valueOf(50)));
    }

    @Test
    void deleteItem_whenItemExists_returnsNoContent() {
        var response = objectUnderTest.deleteItem(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "Item deletion should return HTTP 204");
        verify(itemServiceMock).deleteItem(1L);
    }
}
