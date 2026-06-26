package com.fashionstore.services;

import com.fashionstore.dto.response.ItemResponse;
import com.fashionstore.exceptions.ConflictException;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.repositories.CartItemRepository;
import com.fashionstore.repositories.CategoryRepository;
import com.fashionstore.repositories.FavoriteRepository;
import com.fashionstore.repositories.ItemRepository;
import com.fashionstore.repositories.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest extends ServiceTestSupport {
    @Mock private ItemRepository itemRepositoryMock;
    @Mock private CategoryRepository categoryRepositoryMock;
    @Mock private CartItemRepository cartItemRepositoryMock;
    @Mock private FavoriteRepository favoriteRepositoryMock;
    @Mock private ReviewRepository reviewRepositoryMock;
    private ItemService objectUnderTest;

    @BeforeEach
    void setUp() {
        objectUnderTest = new ItemService(itemRepositoryMock, categoryRepositoryMock, cartItemRepositoryMock,
                favoriteRepositoryMock, reviewRepositoryMock);
    }

    @Test
    void createItem_whenCategoryExists_returnsCreatedItem() {
        var category = category(1L, "Shirts");
        var createdItem = item(1L, "Service Shirt", category);

        when(categoryRepositoryMock.findById(1L)).thenReturn(Optional.of(category));
        when(itemRepositoryMock.save(any())).thenReturn(createdItem);

        ItemResponse response = objectUnderTest.createItem(itemRequest("Service Shirt", 1L));

        assertEquals("Service Shirt", response.getName(), "Created item name should match saved entity");
        assertEquals("https://example.com/item.png", response.getImageUrl(), "Created item image should match saved entity");
    }

    @Test
    void updateItem_whenItemExists_returnsUpdatedItem() {
        var category = category(1L, "Shirts");
        var createdItem = item(1L, "Service Shirt", category);
        var updatedItem = item(1L, "Updated Shirt", category);

        when(itemRepositoryMock.findById(1L)).thenReturn(Optional.of(createdItem));
        when(categoryRepositoryMock.findById(1L)).thenReturn(Optional.of(category));
        when(itemRepositoryMock.save(createdItem)).thenReturn(updatedItem);

        ItemResponse response = objectUnderTest.updateItem(1L, itemRequest("Updated Shirt", 1L));

        assertEquals("Updated Shirt", response.getName(), "Updated item name should match saved entity");
        assertEquals("https://example.com/item.png", response.getImageUrl(), "Updated item image should match saved entity");
    }

    @Test
    void getItemsByCategory_whenItemsExist_returnsCategoryItems() {
        var category = category(1L, "Shirts");
        var item = item(1L, "Updated Shirt", category);

        when(itemRepositoryMock.findByCategoryId(1L)).thenReturn(List.of(item));

        var response = objectUnderTest.getItemsByCategory(1L);

        assertEquals(1, response.size(), "Category query should return repository items");
    }

    @Test
    void getItemById_whenItemIsMissing_throwsNotFoundException() {
        when(itemRepositoryMock.findById(99999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> objectUnderTest.getItemById(99999L));
    }

    @Test
    void deleteItem_whenItemIsUnused_deletesItem() {
        when(itemRepositoryMock.existsById(1L)).thenReturn(true);
        when(cartItemRepositoryMock.existsByItemVariantItemId(1L)).thenReturn(false);
        when(favoriteRepositoryMock.existsByItemVariantItemId(1L)).thenReturn(false);
        when(reviewRepositoryMock.existsByItemVariantItemId(1L)).thenReturn(false);

        objectUnderTest.deleteItem(1L);

        verify(itemRepositoryMock).deleteById(1L);
    }

    @Test
    void deleteItem_whenItemHasCartItems_throwsConflictException() {
        when(itemRepositoryMock.existsById(1L)).thenReturn(true);
        when(cartItemRepositoryMock.existsByItemVariantItemId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> objectUnderTest.deleteItem(1L));
        verify(itemRepositoryMock, never()).deleteById(1L);
    }

    @Test
    void deleteItem_whenItemHasFavourites_throwsConflictException() {
        when(itemRepositoryMock.existsById(1L)).thenReturn(true);
        when(cartItemRepositoryMock.existsByItemVariantItemId(1L)).thenReturn(false);
        when(favoriteRepositoryMock.existsByItemVariantItemId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> objectUnderTest.deleteItem(1L));
        verify(itemRepositoryMock, never()).deleteById(1L);
    }

    @Test
    void deleteItem_whenItemHasReviews_throwsConflictException() {
        when(itemRepositoryMock.existsById(1L)).thenReturn(true);
        when(cartItemRepositoryMock.existsByItemVariantItemId(1L)).thenReturn(false);
        when(favoriteRepositoryMock.existsByItemVariantItemId(1L)).thenReturn(false);
        when(reviewRepositoryMock.existsByItemVariantItemId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> objectUnderTest.deleteItem(1L));
        verify(itemRepositoryMock, never()).deleteById(1L);
    }
}
