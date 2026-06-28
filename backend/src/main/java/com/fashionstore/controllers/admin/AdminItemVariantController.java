package com.fashionstore.controllers.admin;

import com.fashionstore.dto.request.BulkDeleteRequest;
import com.fashionstore.dto.request.ItemVariantRequest;
import com.fashionstore.dto.response.AdminItemVariantResponse;
import com.fashionstore.dto.response.ItemVariantResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.services.ItemVariantService;
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
@RequestMapping("/api/admin/item-variants")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Item Variants", description = "Admin item variant management")
public class AdminItemVariantController {
    private final ItemVariantService itemVariantService;

    @GetMapping
    @Operation(summary = "Get paged item variants with admin details")
    public ResponseEntity<PageResponse<AdminItemVariantResponse>> getPagedItemVariants(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String filterColumn,
            @RequestParam(required = false) String filterValue) {
        return ResponseEntity.ok(itemVariantService.getPagedAdminItemVariants(page, size, search, filterColumn, filterValue));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item variant by id")
    public ResponseEntity<ItemVariantResponse> getItemVariantById(@PathVariable Long id) {
        return ResponseEntity.ok(itemVariantService.getItemVariantById(id));
    }

    @PostMapping
    @Operation(summary = "Create item variant")
    public ResponseEntity<ItemVariantResponse> createItemVariant(@Valid @RequestBody ItemVariantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemVariantService.createItemVariant(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update item variant by id")
    public ResponseEntity<ItemVariantResponse> updateItemVariant(@PathVariable Long id, @Valid @RequestBody ItemVariantRequest request) {
        return ResponseEntity.ok(itemVariantService.updateItemVariant(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete item variant by id")
    public ResponseEntity<Void> deleteItemVariant(@PathVariable Long id) {
        itemVariantService.deleteItemVariant(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk-delete")
    @Operation(summary = "Delete multiple item variants")
    public ResponseEntity<Void> deleteItemVariants(@Valid @RequestBody BulkDeleteRequest request) {
        itemVariantService.deleteItemVariants(request.getIds());
        return ResponseEntity.noContent().build();
    }
}
