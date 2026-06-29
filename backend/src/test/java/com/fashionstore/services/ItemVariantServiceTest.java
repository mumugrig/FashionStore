package com.fashionstore.services;

import com.fashionstore.dto.response.ItemVariantResponse;
import com.fashionstore.exceptions.ConflictException;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.repositories.CartItemRepository;
import com.fashionstore.repositories.ColorRepository;
import com.fashionstore.repositories.FavoriteRepository;
import com.fashionstore.repositories.ItemRepository;
import com.fashionstore.repositories.ItemVariantRepository;
import com.fashionstore.repositories.SizeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    @Mock private ItemVariantRepository itemVariantRepositoryMock;
    @Mock private ItemRepository itemRepositoryMock;
    @Mock private SizeRepository sizeRepositoryMock;
    @Mock private ColorRepository colorRepositoryMock;
    @Mock private CartItemRepository cartItemRepositoryMock;
    @Mock private FavoriteRepository favoriteRepositoryMock;
    private ItemVariantService objectUnderTest;

    @BeforeEach
    void setUp() {
        objectUnderTest = new ItemVariantService(itemVariantRepositoryMock, itemRepositoryMock, sizeRepositoryMock,
                colorRepositoryMock, cartItemRepositoryMock, favoriteRepositoryMock);
    }

    @Test
    void createItemVariant_whenReferencesExist_returnsCreatedVariant() {
        var category = category(1L, "Shirts");
        var item = item(1L, "Variant Shirt", category);
        var size = size(1L, "M");
        var color = color(1L, "Blue", "#0000ff");
        var variant = itemVariant(1L, item, size, color);

        when(itemRepositoryMock.findById(1L)).thenReturn(Optional.of(item));
        when(sizeRepositoryMock.findById(1L)).thenReturn(Optional.of(size));
        when(colorRepositoryMock.findById(1L)).thenReturn(Optional.of(color));
        when(itemVariantRepositoryMock.save(any())).thenReturn(variant);

        ItemVariantResponse response = objectUnderTest.createItemVariant(itemVariantRequest(1L, 1L, 1L));

        assertEquals(item.getId(), response.getItemId(), "Created variant should reference the requested item");
        assertEquals("https://example.com/variant.png", response.getImageUrl(), "Created variant image should match saved entity");
    }

    @Test
    void updateItemVariant_whenVariantExists_returnsUpdatedVariant() {
        var category = category(1L, "Shirts");
        var item = item(1L, "Variant Shirt", category);
        var size = size(1L, "M");
        var color = color(1L, "Blue", "#0000ff");
        var variant = itemVariant(1L, item, size, color);
        var inactiveVariant = itemVariant(1L, item, size, color);
        inactiveVariant.setActive(false);

        when(itemVariantRepositoryMock.findById(1L)).thenReturn(Optional.of(variant));
        when(itemRepositoryMock.findById(1L)).thenReturn(Optional.of(item));
        when(sizeRepositoryMock.findById(1L)).thenReturn(Optional.of(size));
        when(colorRepositoryMock.findById(1L)).thenReturn(Optional.of(color));
        when(itemVariantRepositoryMock.save(variant)).thenReturn(inactiveVariant);

        ItemVariantResponse response = objectUnderTest.updateItemVariant(1L, itemVariantRequest(1L, 1L, 1L, false, 3));

        assertFalse(response.isActive(), "Updated variant should reflect saved active flag");
        assertEquals("https://example.com/variant.png", response.getImageUrl(), "Updated variant image should match saved entity");
    }

    @Test
    void getActiveVariantsByItem_whenActiveVariantsExist_returnsActiveVariants() {
        var category = category(1L, "Shirts");
        var item = item(1L, "Variant Shirt", category);
        var size = size(1L, "M");
        var color = color(1L, "Blue", "#0000ff");
        var variant = itemVariant(1L, item, size, color);

        when(itemVariantRepositoryMock.findByItemIdAndIsActiveTrue(1L)).thenReturn(List.of(variant));

        var response = objectUnderTest.getActiveVariantsByItem(1L);

        assertEquals(1, response.size(), "Active variant query should return repository variants");
    }

    @Test
    void getItemVariantById_whenVariantIsMissing_throwsNotFoundException() {
        when(itemVariantRepositoryMock.findById(99999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> objectUnderTest.getItemVariantById(99999L));
    }

    @Test
    void deleteItemVariant_whenVariantIsUnused_deletesVariant() {
        when(itemVariantRepositoryMock.existsById(1L)).thenReturn(true);
        when(cartItemRepositoryMock.existsByItemVariantId(1L)).thenReturn(false);
        when(favoriteRepositoryMock.existsByItemVariantId(1L)).thenReturn(false);

        objectUnderTest.deleteItemVariant(1L);

        verify(itemVariantRepositoryMock).deleteById(1L);
    }

    @Test
    void deleteItemVariant_whenVariantIsInCart_throwsConflictException() {
        when(itemVariantRepositoryMock.existsById(1L)).thenReturn(true);
        when(cartItemRepositoryMock.existsByItemVariantId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> objectUnderTest.deleteItemVariant(1L));
        verify(itemVariantRepositoryMock, never()).deleteById(1L);
    }

    @Test
    void deleteItemVariant_whenVariantIsInFavourites_throwsConflictException() {
        when(itemVariantRepositoryMock.existsById(1L)).thenReturn(true);
        when(cartItemRepositoryMock.existsByItemVariantId(1L)).thenReturn(false);
        when(favoriteRepositoryMock.existsByItemVariantId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> objectUnderTest.deleteItemVariant(1L));
        verify(itemVariantRepositoryMock, never()).deleteById(1L);
    }

}
