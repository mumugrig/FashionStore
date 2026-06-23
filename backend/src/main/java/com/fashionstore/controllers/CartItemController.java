package com.fashionstore.controllers;

import com.fashionstore.dto.request.CartItemRequest;
import com.fashionstore.dto.response.CartItemResponse;
import com.fashionstore.services.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartItemController {
    private  final CartItemService cartItemService;

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCartItems() {
        List<CartItemResponse> cartItemResponse = cartItemService.getAllCartItems();
        return ResponseEntity.ok(cartItemResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartItemResponse> updateCartItem(@PathVariable Long id, @RequestBody CartItemRequest cartItemRequest) {
        CartItemResponse updatedCartItem = cartItemService.updateCartItem(id, cartItemRequest);
        return ResponseEntity.ok(updatedCartItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long id) {
        cartItemService.removeFromCart(id);
        return ResponseEntity.noContent().build();
    }
}
