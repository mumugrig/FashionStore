package com.fashionstore.controllers.admin;

import com.fashionstore.dto.request.BulkDeleteRequest;
import com.fashionstore.dto.request.SizeRequest;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.dto.response.SizeResponse;
import com.fashionstore.services.SizeService;
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
@RequestMapping("/api/admin/sizes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Sizes", description = "Admin size management")
public class AdminSizeController {
    private final SizeService sizeService;

    @GetMapping
    @Operation(summary = "Get paged sizes")
    public ResponseEntity<PageResponse<SizeResponse>> getPagedSizes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String filterColumn,
            @RequestParam(required = false) String filterValue) {
        return ResponseEntity.ok(sizeService.getPagedSizes(page, size, search, filterColumn, filterValue));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get size by id")
    public ResponseEntity<SizeResponse> getSizeById(@PathVariable Long id) {
        return ResponseEntity.ok(sizeService.getSizeById(id));
    }

    @PostMapping
    @Operation(summary = "Create size")
    public ResponseEntity<SizeResponse> createSize(@Valid @RequestBody SizeRequest sizeRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sizeService.createSize(sizeRequest));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update size by id")
    public ResponseEntity<SizeResponse> updateSize(@PathVariable Long id, @Valid @RequestBody SizeRequest sizeRequest) {
        return ResponseEntity.ok(sizeService.updateSize(id, sizeRequest));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete size by id")
    public ResponseEntity<Void> deleteSize(@PathVariable Long id) {
        sizeService.deleteSize(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk-delete")
    @Operation(summary = "Delete multiple sizes")
    public ResponseEntity<Void> deleteSizes(@Valid @RequestBody BulkDeleteRequest request) {
        sizeService.deleteSizes(request.getIds());
        return ResponseEntity.noContent().build();
    }
}
