package com.fashionstore.controllers.user;

import com.fashionstore.dto.response.ItemResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.services.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Items", description = "Catalog item browsing")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    @Operation(summary = "Get paged items", description = "Returns catalog items with optional category, search, size, color, audience, and price filters.")
    public ResponseEntity<PageResponse<ItemResponse>> getPagedItems(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String itemSize,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String audience,
            @RequestParam(required = false) BigDecimal pricemin,
            @RequestParam(required = false) BigDecimal pricemax,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax) {
        return ResponseEntity.ok(itemService.getPagedItems(
                page, size, category, search, itemSize, color, audience,
                priceMin != null ? priceMin : pricemin,
                priceMax != null ? priceMax : pricemax));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item by id")
    public ResponseEntity<ItemResponse> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }
}
