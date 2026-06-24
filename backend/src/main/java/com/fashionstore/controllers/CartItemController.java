package com.fashionstore.controllers;

import com.fashionstore.dto.request.CartItemRequest;
import com.fashionstore.dto.response.CartItemResponse;
import com.fashionstore.services.CartItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartItemController {
    private final CartItemService cartItemService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CartItemResponse>> getCartItems() {
        List<CartItemResponse> cartItemResponse = cartItemService.getAllCartItems();
        return ResponseEntity.ok(cartItemResponse);
    }

    @PostMapping("/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartItemResponse> addCartItem(@Valid @RequestBody CartItemRequest cartItemRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemService.addToCart(cartItemRequest));
    }

    @PutMapping("/items/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartItemResponse> updateCartItem(@PathVariable Long id, @Valid @RequestBody CartItemRequest cartItemRequest) {
        CartItemResponse updatedCartItem = cartItemService.updateCartItem(id, cartItemRequest);
        return ResponseEntity.ok(updatedCartItem);
    }

    @DeleteMapping("/items/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long id) {
        cartItemService.removeFromCart(id);
        return ResponseEntity.noContent().build();
    }
}
