package com.fashionstore.controllers.user;

import com.fashionstore.dto.request.CartItemRequest;
import com.fashionstore.dto.response.CartItemResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.services.CartItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Cart", description = "Current user's shopping cart")
public class CartController {
    private final CartItemService cartItemService;

    @GetMapping
    @Operation(summary = "Get current user's paged cart items")
    public ResponseEntity<PageResponse<CartItemResponse>> getPagedCartItems(
            Authentication authentication,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(cartItemService.getPagedCartItems(authentication, page, size, search));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item variant to current user's cart")
    public ResponseEntity<CartItemResponse> addCartItem(Authentication authentication, @Valid @RequestBody CartItemRequest cartItemRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemService.addToCart(authentication, cartItemRequest));
    }

    @PutMapping("/items/{id}")
    @Operation(summary = "Update current user's cart item")
    public ResponseEntity<CartItemResponse> updateCartItem(Authentication authentication, @PathVariable Long id, @Valid @RequestBody CartItemRequest cartItemRequest) {
        return ResponseEntity.ok(cartItemService.updateCartItem(authentication, id, cartItemRequest));
    }

    @DeleteMapping("/items/{id}")
    @Operation(summary = "Delete current user's cart item")
    public ResponseEntity<Void> deleteCartItem(Authentication authentication, @PathVariable Long id) {
        cartItemService.removeFromCart(authentication, id);
        return ResponseEntity.noContent().build();
    }
}
