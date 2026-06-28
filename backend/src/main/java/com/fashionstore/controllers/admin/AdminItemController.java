package com.fashionstore.controllers.admin;

import com.fashionstore.dto.request.BulkDeleteRequest;
import com.fashionstore.dto.request.ItemRequest;
import com.fashionstore.dto.response.AdminItemResponse;
import com.fashionstore.dto.response.ItemResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.services.ItemService;
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
@RequestMapping("/api/admin/items")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Items", description = "Admin item management")
public class AdminItemController {
    private final ItemService itemService;

    @GetMapping
    @Operation(summary = "Get paged items with admin details")
    public ResponseEntity<PageResponse<AdminItemResponse>> getPagedItems(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String filterColumn,
            @RequestParam(required = false) String filterValue) {
        return ResponseEntity.ok(itemService.getPagedAdminItems(page, size, search, filterColumn, filterValue));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item by id")
    public ResponseEntity<ItemResponse> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    @PostMapping
    @Operation(summary = "Create item")
    public ResponseEntity<ItemResponse> createItem(@Valid @RequestBody ItemRequest itemRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.createItem(itemRequest));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update item by id")
    public ResponseEntity<ItemResponse> updateItem(@PathVariable Long id, @Valid @RequestBody ItemRequest itemRequest) {
        return ResponseEntity.ok(itemService.updateItem(id, itemRequest));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete item by id")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk-delete")
    @Operation(summary = "Delete multiple items")
    public ResponseEntity<Void> deleteItems(@Valid @RequestBody BulkDeleteRequest request) {
        itemService.deleteItems(request.getIds());
        return ResponseEntity.noContent().build();
    }
}
