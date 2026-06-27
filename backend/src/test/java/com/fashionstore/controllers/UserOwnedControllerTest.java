package com.fashionstore.controllers;

import com.fashionstore.services.AddressService;
import com.fashionstore.services.CartItemService;
import com.fashionstore.services.FavoriteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserOwnedControllerTest extends ControllerTestSupport {
    @Mock private CartItemService cartItemServiceMock;
    @Mock private FavoriteService favoriteServiceMock;
    @Mock private AddressService addressServiceMock;

    @Test
    void cartEndpoints_callAuthenticatedUserServiceMethods() {
        var controller = new com.fashionstore.controllers.user.CartController(cartItemServiceMock);
        Authentication authentication = authentication();
        when(cartItemServiceMock.getPagedCartItems(authentication, 1, 20, null))
                .thenReturn(pageResponse(cartItemResponse(1L, 1L, 2L, 3)));
        when(cartItemServiceMock.addToCart(eq(authentication), any()))
                .thenReturn(cartItemResponse(1L, 1L, 2L, 3));
        when(cartItemServiceMock.updateCartItem(eq(authentication), eq(1L), any()))
                .thenReturn(cartItemResponse(1L, 1L, 2L, 4));

        assertEquals(1, controller.getPagedCartItems(authentication, 1, 20, null).getBody().getContent().size());
        assertEquals(HttpStatus.CREATED, controller.addCartItem(authentication, cartItemRequest(99L, 2L, 3)).getStatusCode());
        assertEquals(4, controller.updateCartItem(authentication, 1L, cartItemRequest(99L, 2L, 4)).getBody().getQuantity());
        assertEquals(HttpStatus.NO_CONTENT, controller.deleteCartItem(authentication, 1L).getStatusCode());

        verify(cartItemServiceMock).removeFromCart(authentication, 1L);
    }

    @Test
    void favoriteEndpoints_callAuthenticatedUserServiceMethods() {
        var controller = new com.fashionstore.controllers.user.FavoriteController(favoriteServiceMock);
        Authentication authentication = authentication();
        when(favoriteServiceMock.getPagedFavorites(authentication, 1, 20, null))
                .thenReturn(pageResponse(favoriteResponse(1L, 1L, 2L)));
        when(favoriteServiceMock.addFavorite(eq(authentication), any()))
                .thenReturn(favoriteResponse(1L, 1L, 2L));

        assertEquals(1, controller.getPagedFavorites(authentication, 1, 20, null).getBody().getContent().size());
        assertEquals(HttpStatus.CREATED, controller.addFavorite(authentication, favoriteRequest(99L, 2L)).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, controller.removeFavorite(authentication, 1L).getStatusCode());

        verify(favoriteServiceMock).deleteFavorite(authentication, 1L);
    }

    @Test
    void addressEndpoints_callAuthenticatedUserServiceMethods() {
        var controller = new com.fashionstore.controllers.user.AddressController(addressServiceMock);
        Authentication authentication = authentication();
        when(addressServiceMock.getPagedAddresses(authentication, 1, 20))
                .thenReturn(pageResponse(addressResponse(1L, 1L, "Kyiv")));
        when(addressServiceMock.getAddressById(authentication, 1L))
                .thenReturn(addressResponse(1L, 1L, "Kyiv"));
        when(addressServiceMock.addAddress(eq(authentication), any()))
                .thenReturn(addressResponse(1L, 1L, "Kyiv"));
        when(addressServiceMock.updateAddress(eq(authentication), eq(1L), any()))
                .thenReturn(addressResponse(1L, 1L, "Lviv"));

        assertEquals(1, controller.getPagedAddresses(authentication, 1, 20).getBody().getContent().size());
        assertEquals("Kyiv", controller.getAddressById(authentication, 1L).getBody().getCity());
        assertEquals(HttpStatus.CREATED, controller.createAddress(authentication, addressRequest(99L, "Kyiv")).getStatusCode());
        assertEquals("Lviv", controller.updateAddress(authentication, 1L, addressRequest(99L, "Lviv")).getBody().getCity());
        assertEquals(HttpStatus.NO_CONTENT, controller.deleteAddress(authentication, 1L).getStatusCode());

        verify(addressServiceMock).deleteAddress(authentication, 1L);
    }

    private Authentication authentication() {
        return new TestingAuthenticationToken("user@example.com", null);
    }
}
