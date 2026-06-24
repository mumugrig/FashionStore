package com.fashionstore.controllers;

import com.fashionstore.dto.request.ItemRequest;
import com.fashionstore.dto.response.ItemResponse;
import com.fashionstore.services.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ItemResponse>> getAllItems() {
        List<ItemResponse> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ItemResponse> getItemById(@PathVariable Long id) {
        ItemResponse item = itemService.getItemById(id);
        return ResponseEntity.ok(item);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ItemResponse> createItem(@Valid @RequestBody ItemRequest itemRequest) {
        ItemResponse createdItem = itemService.createItem(itemRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ItemResponse> updateItem(@PathVariable Long id, @Valid @RequestBody ItemRequest itemRequest){
        ItemResponse updatedItem = itemService.updateItem(id, itemRequest);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id){
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
