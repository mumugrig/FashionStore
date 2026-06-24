package com.fashionstore.services;

import com.fashionstore.dto.response.ItemResponse;
import com.fashionstore.exceptions.ConflictException;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.repositories.CartItemRepository;
import com.fashionstore.repositories.CategoryRepository;
import com.fashionstore.repositories.FavoriteRepository;
import com.fashionstore.repositories.ItemRepository;
import com.fashionstore.repositories.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
    @Mock private ItemRepository itemRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private FavoriteRepository favoriteRepository;
    @Mock private ReviewRepository reviewRepository;
    @InjectMocks private ItemService itemService;

    @Test
    void createsUpdatesAndQueriesItemsByCategory() {
        var category = category(1L, "Shirts");
        var createdItem = item(1L, "Service Shirt", category);
        var updatedItem = item(1L, "Updated Shirt", category);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(itemRepository.save(any())).thenReturn(createdItem);
        ItemResponse created = itemService.createItem(itemRequest("Service Shirt", 1L));

        when(itemRepository.findById(created.getId())).thenReturn(Optional.of(createdItem));
        when(itemRepository.save(createdItem)).thenReturn(updatedItem);
        assertEquals("Updated Shirt", itemService.updateItem(created.getId(), itemRequest("Updated Shirt", 1L)).getName());

        when(itemRepository.findByCategoryId(1L)).thenReturn(List.of(updatedItem));
        assertEquals(1, itemService.getItemsByCategory(1L).size());
    }

    @Test
    void throwsWhenItemIsMissing() {
        when(itemRepository.findById(99999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(99999L));
    }

    @Test
    void deletesItem() {
        when(cartItemRepository.existsByItemVariantItemId(1L)).thenReturn(false);
        when(favoriteRepository.existsByItemVariantItemId(1L)).thenReturn(false);
        when(reviewRepository.existsByItemVariantItemId(1L)).thenReturn(false);

        itemService.deleteItem(1L);

        verify(itemRepository).deleteById(1L);
    }

    @Test
    void blocksDeletingItemWithCartItems() {
        when(cartItemRepository.existsByItemVariantItemId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> itemService.deleteItem(1L));
        verify(itemRepository, never()).deleteById(1L);
    }

    @Test
    void blocksDeletingItemWithFavourites() {
        when(cartItemRepository.existsByItemVariantItemId(1L)).thenReturn(false);
        when(favoriteRepository.existsByItemVariantItemId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> itemService.deleteItem(1L));
        verify(itemRepository, never()).deleteById(1L);
    }

    @Test
    void blocksDeletingItemWithReviews() {
        when(cartItemRepository.existsByItemVariantItemId(1L)).thenReturn(false);
        when(favoriteRepository.existsByItemVariantItemId(1L)).thenReturn(false);
        when(reviewRepository.existsByItemVariantItemId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> itemService.deleteItem(1L));
        verify(itemRepository, never()).deleteById(1L);
    }
}
