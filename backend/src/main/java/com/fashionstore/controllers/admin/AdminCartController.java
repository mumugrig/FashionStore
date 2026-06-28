package com.fashionstore.controllers.admin;

import com.fashionstore.dto.request.BulkDeleteRequest;
import com.fashionstore.dto.request.CartItemRequest;
import com.fashionstore.dto.response.AdminCartItemResponse;
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
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Cart", description = "Admin cart item management")
public class AdminCartController {
    private final CartItemService cartItemService;

    @GetMapping("/cart")
    @Operation(summary = "Get paged cart items with admin details")
    public ResponseEntity<PageResponse<AdminCartItemResponse>> getPagedCartItems(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String filterColumn,
            @RequestParam(required = false) String filterValue) {
        return ResponseEntity.ok(cartItemService.getPagedAdminCartItems(page, size, search, filterColumn, filterValue));
    }

    @GetMapping("/users/{userId}/cart")
    @Operation(summary = "Get paged cart items for a user")
    public ResponseEntity<PageResponse<CartItemResponse>> getPagedCartItemsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(cartItemService.getPagedCartItemsByUser(userId, page, size));
    }

    @GetMapping("/cart/items/{id}")
    @Operation(summary = "Get cart item by id")
    public ResponseEntity<AdminCartItemResponse> getCartItemById(@PathVariable Long id) {
        return ResponseEntity.ok(cartItemService.getAdminCartItemById(id));
    }

    @PostMapping("/cart")
    @Operation(summary = "Create cart item")
    public ResponseEntity<CartItemResponse> addCartItem(@Valid @RequestBody CartItemRequest cartItemRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemService.addToCart(cartItemRequest));
    }

    @PutMapping("/cart/items/{id}")
    @Operation(summary = "Update cart item by id")
    public ResponseEntity<CartItemResponse> updateCartItem(@PathVariable Long id, @Valid @RequestBody CartItemRequest cartItemRequest) {
        return ResponseEntity.ok(cartItemService.updateCartItem(id, cartItemRequest));
    }

    @DeleteMapping("/cart/items/{id}")
    @Operation(summary = "Delete cart item by id")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long id) {
        cartItemService.removeFromCart(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cart/bulk-delete")
    @Operation(summary = "Delete multiple cart items")
    public ResponseEntity<Void> deleteCartItems(@Valid @RequestBody BulkDeleteRequest request) {
        cartItemService.removeCartItems(request.getIds());
        return ResponseEntity.noContent().build();
    }
}
