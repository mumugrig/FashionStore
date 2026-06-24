package com.fashionstore.controllers;

import com.fashionstore.services.CartItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartItemControllerTest extends ControllerTestSupport {
    @Mock private CartItemService cartItemService;
    @InjectMocks private CartItemController cartItemController;

    @Test
    void listsUpdatesAndDeletesCartItems() {
        when(cartItemService.updateCartItem(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any())).thenReturn(cartItemResponse(1L, 1L, 2L, 3));
        when(cartItemService.getAllCartItems()).thenReturn(List.of(cartItemResponse(1L, 1L, 2L, 3)));

        var updated = cartItemController.updateCartItem(1L, cartItemRequest(1L, 2L, 3));
        var listed = cartItemController.getCartItems();
        var deleted = cartItemController.deleteCartItem(1L);

        assertEquals(HttpStatus.OK, updated.getStatusCode());
        assertEquals(3, updated.getBody().getQuantity());
        assertEquals(1, listed.getBody().size());
        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());
        verify(cartItemService).removeFromCart(1L);
    }
}
