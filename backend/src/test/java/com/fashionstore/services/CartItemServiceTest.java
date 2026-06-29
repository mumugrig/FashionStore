package com.fashionstore.services;

import com.fashionstore.dto.response.CartItemResponse;
import com.fashionstore.exceptions.ConflictException;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.repositories.CartItemRepository;
import com.fashionstore.repositories.ItemVariantRepository;
import com.fashionstore.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest extends ServiceTestSupport {
    @Mock private CartItemRepository cartItemRepositoryMock;
    @Mock private ItemVariantRepository itemVariantRepositoryMock;
    @Mock private UserRepository userRepositoryMock;
    @Mock private CurrentUserService currentUserServiceMock;
    private CartItemService objectUnderTest;

    @BeforeEach
    void setUp() {
        objectUnderTest = new CartItemService(cartItemRepositoryMock, itemVariantRepositoryMock, userRepositoryMock, currentUserServiceMock);
    }

    @Test
    void addToCart_whenUserAndVariantExist_returnsCreatedCartItem() {
        var user = user(1L, "cart-service@example.com");
        var category = category(1L, "Shirts");
        var item = item(1L, "Cart Shirt", category);
        var size = size(1L, "M");
        var color = color(1L, "Blue", "#0000ff");
        var variant = itemVariant(1L, item, size, color);
        var createdCartItem = cartItem(1L, user, variant, 2);

        when(itemVariantRepositoryMock.findById(variant.getId())).thenReturn(Optional.of(variant));
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(cartItemRepositoryMock.save(any())).thenReturn(createdCartItem);

        CartItemResponse response = objectUnderTest.addToCart(cartItemRequest(user.getId(), variant.getId(), 2));

        assertEquals(2, response.getQuantity(), "Created cart item quantity should match saved entity");
    }

    @Test
    void addToCart_whenVariantIsOutOfStock_throwsConflictException() {
        var user = user(1L, "cart-service@example.com");
        var category = category(1L, "Shirts");
        var item = item(1L, "Cart Shirt", category);
        var size = size(1L, "M");
        var color = color(1L, "Blue", "#0000ff");
        var variant = itemVariant(1L, item, size, color);
        variant.setStockLeft(0);

        when(itemVariantRepositoryMock.findById(variant.getId())).thenReturn(Optional.of(variant));

        assertThrows(ConflictException.class, () -> objectUnderTest.addToCart(cartItemRequest(user.getId(), variant.getId(), 1)));
    }

    @Test
    void addToCart_whenQuantityExceedsStock_throwsConflictException() {
        var user = user(1L, "cart-service@example.com");
        var category = category(1L, "Shirts");
        var item = item(1L, "Cart Shirt", category);
        var size = size(1L, "M");
        var color = color(1L, "Blue", "#0000ff");
        var variant = itemVariant(1L, item, size, color);
        variant.setStockLeft(1);

        when(itemVariantRepositoryMock.findById(variant.getId())).thenReturn(Optional.of(variant));

        assertThrows(ConflictException.class, () -> objectUnderTest.addToCart(cartItemRequest(user.getId(), variant.getId(), 2)));
    }

    @Test
    void updateCartItem_whenCartItemExists_returnsUpdatedCartItem() {
        var user = user(1L, "cart-service@example.com");
        var category = category(1L, "Shirts");
        var item = item(1L, "Cart Shirt", category);
        var size = size(1L, "M");
        var color = color(1L, "Blue", "#0000ff");
        var variant = itemVariant(1L, item, size, color);
        var createdCartItem = cartItem(1L, user, variant, 2);
        var updatedCartItem = cartItem(1L, user, variant, 4);

        when(cartItemRepositoryMock.findById(1L)).thenReturn(Optional.of(createdCartItem));
        when(itemVariantRepositoryMock.findById(variant.getId())).thenReturn(Optional.of(variant));
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(cartItemRepositoryMock.save(createdCartItem)).thenReturn(updatedCartItem);

        CartItemResponse response = objectUnderTest.updateCartItem(1L, cartItemRequest(user.getId(), variant.getId(), 4));

        assertEquals(4, response.getQuantity(), "Updated cart item quantity should match saved entity");
    }

    @Test
    void updateCartItem_whenAuthenticatedUserDoesNotOwnCartItem_throwsNotFoundException() {
        var user = user(1L, "cart-owner@example.com");
        Authentication authentication = authentication(user.getId());
        when(currentUserServiceMock.findCurrentUser(authentication)).thenReturn(user);
        when(cartItemRepositoryMock.findByIdAndUserId(1L, user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> objectUnderTest.updateCartItem(authentication, 1L, cartItemRequest(99L, 2L, 4)));
    }

    @Test
    void getPagedCartItemsByUser_whenCartItemsExist_returnsPagedCartItems() {
        var user = user(1L, "cart-service@example.com");
        var category = category(1L, "Shirts");
        var item = item(1L, "Cart Shirt", category);
        var size = size(1L, "M");
        var color = color(1L, "Blue", "#0000ff");
        var variant = itemVariant(1L, item, size, color);
        var cartItem = cartItem(1L, user, variant, 4);

        when(cartItemRepositoryMock.findByUserId(org.mockito.ArgumentMatchers.eq(user.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(cartItem)));

        var response = objectUnderTest.getPagedCartItemsByUser(user.getId(), 1, 20);

        assertEquals(1, response.getContent().size(), "Cart item page should contain repository results");
    }

    @Test
    void getPagedCartItems_whenSearchIsProvided_filtersCurrentUserCartByItemName() {
        var user = user(1L, "cart-search@example.com");
        var category = category(1L, "Shirts");
        var item = item(1L, "Search Shirt", category);
        var size = size(1L, "M");
        var color = color(1L, "Blue", "#0000ff");
        var variant = itemVariant(1L, item, size, color);
        var cartItem = cartItem(1L, user, variant, 4);
        Authentication authentication = authentication(user.getId());

        when(currentUserServiceMock.findCurrentUser(authentication)).thenReturn(user);
        when(cartItemRepositoryMock.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(cartItem)));

        var response = objectUnderTest.getPagedCartItems(authentication, 1, 20, "shirt");

        assertEquals(1, response.getContent().size(), "Cart search should return repository results");
        verify(cartItemRepositoryMock).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void clearUserCart_whenUserExists_deletesUserCartItems() {
        var user = user(1L, "cart-service@example.com");
        var category = category(1L, "Shirts");
        var item = item(1L, "Cart Shirt", category);
        var size = size(1L, "M");
        var color = color(1L, "Blue", "#0000ff");
        var variant = itemVariant(1L, item, size, color);
        var cartItem = cartItem(1L, user, variant, 4);

        when(userRepositoryMock.existsById(user.getId())).thenReturn(true);
        when(cartItemRepositoryMock.findByUserId(user.getId())).thenReturn(List.of(cartItem));

        objectUnderTest.clearUserCart(user.getId());

        verify(cartItemRepositoryMock).deleteAll(List.of(cartItem));
    }

    @Test
    void removeFromCart_whenAuthenticatedUserDoesNotOwnCartItem_throwsNotFoundException() {
        var user = user(1L, "cart-owner-delete@example.com");
        Authentication authentication = authentication(user.getId());
        when(currentUserServiceMock.findCurrentUser(authentication)).thenReturn(user);
        when(cartItemRepositoryMock.findByIdAndUserId(1L, user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> objectUnderTest.removeFromCart(authentication, 1L));
    }

    private Authentication authentication(Long userId) {
        return new TestingAuthenticationToken("user-" + userId + "@example.com", null);
    }
}
