package com.fashionstore.services;

import com.fashionstore.dto.response.ItemVariantResponse;
import com.fashionstore.exceptions.ConflictException;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.repositories.CartItemRepository;
import com.fashionstore.repositories.ColorRepository;
import com.fashionstore.repositories.FavoriteRepository;
import com.fashionstore.repositories.ItemRepository;
import com.fashionstore.repositories.ItemVariantRepository;
import com.fashionstore.repositories.ReviewRepository;
import com.fashionstore.repositories.SizeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemVariantServiceTest extends ServiceTestSupport {
    @Mock private ItemVariantRepository itemVariantRepository;
    @Mock private ItemRepository itemRepository;
    @Mock private SizeRepository sizeRepository;
    @Mock private ColorRepository colorRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private FavoriteRepository favoriteRepository;
    @Mock private ReviewRepository reviewRepository;
    @InjectMocks private ItemVariantService itemVariantService;

    @Test
    void createsUpdatesAndQueriesActiveVariantsByItem() {
        var category = category(1L, "Shirts");
        var item = item(1L, "Variant Shirt", category);
        var size = size(1L, "M");
        var color = color(1L, "Blue", "#0000ff");
        var variant = itemVariant(1L, item, size, color);
        var inactiveVariant = itemVariant(1L, item, size, color);
        inactiveVariant.setActive(false);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(sizeRepository.findById(1L)).thenReturn(Optional.of(size));
        when(colorRepository.findById(1L)).thenReturn(Optional.of(color));
        when(itemVariantRepository.save(any())).thenReturn(variant);
        ItemVariantResponse created = itemVariantService.createItemVariant(itemVariantRequest(1L, 1L, 1L));

        when(itemVariantRepository.findById(created.getId())).thenReturn(Optional.of(variant));
        when(itemVariantRepository.save(variant)).thenReturn(inactiveVariant);
        ItemVariantResponse updated = itemVariantService.updateItemVariant(created.getId(), itemVariantRequest(1L, 1L, 1L, false, 3));

        when(itemVariantRepository.findByItemIdAndIsActiveTrue(1L)).thenReturn(List.of(variant));
        assertEquals(item.getId(), created.getItemId());
        assertFalse(updated.isActive());
        assertEquals(1, itemVariantService.getActiveVariantsByItem(1L).size());
    }

    @Test
    void throwsWhenVariantIsMissing() {
        when(itemVariantRepository.findById(99999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemVariantService.getItemVariantById(99999L));
    }

    @Test
    void deletesVariant() {
        when(cartItemRepository.existsByItemVariantId(1L)).thenReturn(false);
        when(favoriteRepository.existsByItemVariantId(1L)).thenReturn(false);
        when(reviewRepository.existsByItemVariantId(1L)).thenReturn(false);

        itemVariantService.deleteItemVariant(1L);

        verify(itemVariantRepository).deleteById(1L);
    }

    @Test
    void blocksDeletingVariantInCart() {
        when(cartItemRepository.existsByItemVariantId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> itemVariantService.deleteItemVariant(1L));
        verify(itemVariantRepository, never()).deleteById(1L);
    }

    @Test
    void blocksDeletingVariantInFavourites() {
        when(cartItemRepository.existsByItemVariantId(1L)).thenReturn(false);
        when(favoriteRepository.existsByItemVariantId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> itemVariantService.deleteItemVariant(1L));
        verify(itemVariantRepository, never()).deleteById(1L);
    }

    @Test
    void blocksDeletingVariantWithReviews() {
        when(cartItemRepository.existsByItemVariantId(1L)).thenReturn(false);
        when(favoriteRepository.existsByItemVariantId(1L)).thenReturn(false);
        when(reviewRepository.existsByItemVariantId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> itemVariantService.deleteItemVariant(1L));
        verify(itemVariantRepository, never()).deleteById(1L);
    }
}
