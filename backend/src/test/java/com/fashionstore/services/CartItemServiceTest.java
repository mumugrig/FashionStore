package com.fashionstore.services;

import com.fashionstore.dto.response.CartItemResponse;
import com.fashionstore.repositories.CartItemRepository;
import com.fashionstore.repositories.ItemVariantRepository;
import com.fashionstore.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest extends ServiceTestSupport {
    @Mock private CartItemRepository cartItemRepository;
    @Mock private ItemVariantRepository itemVariantRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private CartItemService cartItemService;

    @Test
    void addsUpdatesQueriesAndClearsUserCart() {
        var user = user(1L, "cart-service@example.com");
        var category = category(1L, "Shirts");
        var item = item(1L, "Cart Shirt", category);
        var size = size(1L, "M");
        var color = color(1L, "Blue", "#0000ff");
        var variant = itemVariant(1L, item, size, color);
        var createdCartItem = cartItem(1L, user, variant, 2);
        var updatedCartItem = cartItem(1L, user, variant, 4);

        when(itemVariantRepository.findById(variant.getId())).thenReturn(Optional.of(variant));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(cartItemRepository.save(any())).thenReturn(createdCartItem);
        CartItemResponse created = cartItemService.addToCart(cartItemRequest(user.getId(), variant.getId(), 2));

        when(cartItemRepository.findById(created.getId())).thenReturn(Optional.of(createdCartItem));
        when(cartItemRepository.save(createdCartItem)).thenReturn(updatedCartItem);
        CartItemResponse updated = cartItemService.updateCartItem(created.getId(), cartItemRequest(user.getId(), variant.getId(), 4));

        when(cartItemRepository.findByUserId(user.getId())).thenReturn(List.of(updatedCartItem), List.of(updatedCartItem), List.of());
        assertEquals(4, updated.getQuantity());
        assertEquals(1, cartItemService.getCartItemsByUser(user.getId()).size());

        cartItemService.clearUserCart(user.getId());

        verify(cartItemRepository).deleteAll(List.of(updatedCartItem));
        assertTrue(cartItemService.getCartItemsByUser(user.getId()).isEmpty());
    }
}
