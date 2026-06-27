package com.fashionstore.controllers.admin;

import com.fashionstore.dto.request.BulkDeleteRequest;
import com.fashionstore.dto.request.ItemVariantRequest;
import com.fashionstore.dto.response.AdminItemVariantResponse;
import com.fashionstore.dto.response.ItemVariantResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.services.ItemVariantService;
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
public class AdminItemVariantController {
    private final ItemVariantService itemVariantService;

    @GetMapping
    public ResponseEntity<PageResponse<AdminItemVariantResponse>> getPagedItemVariants(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String filterColumn,
            @RequestParam(required = false) String filterValue) {
        return ResponseEntity.ok(itemVariantService.getPagedAdminItemVariants(page, size, search, filterColumn, filterValue));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemVariantResponse> getItemVariantById(@PathVariable Long id) {
        return ResponseEntity.ok(itemVariantService.getItemVariantById(id));
    }

    @PostMapping
    public ResponseEntity<ItemVariantResponse> createItemVariant(@Valid @RequestBody ItemVariantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemVariantService.createItemVariant(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemVariantResponse> updateItemVariant(@PathVariable Long id, @Valid @RequestBody ItemVariantRequest request) {
        return ResponseEntity.ok(itemVariantService.updateItemVariant(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItemVariant(@PathVariable Long id) {
        itemVariantService.deleteItemVariant(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk-delete")
    public ResponseEntity<Void> deleteItemVariants(@Valid @RequestBody BulkDeleteRequest request) {
        itemVariantService.deleteItemVariants(request.getIds());
        return ResponseEntity.noContent().build();
    }
}
