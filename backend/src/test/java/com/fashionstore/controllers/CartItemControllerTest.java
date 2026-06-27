package com.fashionstore.controllers;

import com.fashionstore.services.CartItemService;
import com.fashionstore.controllers.admin.AdminCartController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartItemControllerTest extends ControllerTestSupport {
    @Mock private CartItemService cartItemServiceMock;
    private AdminCartController objectUnderTest;

    @BeforeEach
    void setUp() {
        objectUnderTest = new AdminCartController(cartItemServiceMock);
    }

    @Test
    void getCartItems_whenPageIsRequested_returnsPagedCartItems() {
        when(cartItemServiceMock.getPagedAdminCartItems(1, 20, null, null, null)).thenReturn(pageResponse(adminCartItemResponse(1L, 1L, 2L, 3)));

        var response = objectUnderTest.getPagedCartItems(1, 20, null, null, null);

        assertEquals(1, response.getBody().getContent().size(), "Cart page should contain service results");
        assertEquals("Jacket", response.getBody().getContent().get(0).getItemName(), "Admin cart response should include item name");
    }

    @Test
    void updateCartItem_whenRequestIsValid_returnsUpdatedCartItem() {
        when(cartItemServiceMock.updateCartItem(eq(1L), any())).thenReturn(cartItemResponse(1L, 1L, 2L, 3));

        var response = objectUnderTest.updateCartItem(1L, cartItemRequest(1L, 2L, 3));

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Cart item update should return HTTP 200");
        assertEquals(3, response.getBody().getQuantity(), "Updated quantity should match service response");
    }

    @Test
    void deleteCartItem_whenCartItemExists_returnsNoContent() {
        var response = objectUnderTest.deleteCartItem(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "Cart item deletion should return HTTP 204");
        verify(cartItemServiceMock).removeFromCart(1L);
    }
}
