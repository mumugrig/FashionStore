package com.fashionstore.controllers.admin;

import com.fashionstore.dto.request.BulkDeleteRequest;
import com.fashionstore.dto.request.ColorRequest;
import com.fashionstore.dto.response.ColorResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.services.ColorService;
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
@RequestMapping("/api/admin/colors")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Colors", description = "Admin color management")
public class AdminColorController {
    private final ColorService colorService;

    @GetMapping
    @Operation(summary = "Get paged colors")
    public ResponseEntity<PageResponse<ColorResponse>> getPagedColors(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String filterColumn,
            @RequestParam(required = false) String filterValue) {
        return ResponseEntity.ok(colorService.getPagedColors(page, size, search, filterColumn, filterValue));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get color by id")
    public ResponseEntity<ColorResponse> getColorById(@PathVariable Long id) {
        return ResponseEntity.ok(colorService.getColorById(id));
    }

    @PostMapping
    @Operation(summary = "Create color")
    public ResponseEntity<ColorResponse> createColor(@Valid @RequestBody ColorRequest colorRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(colorService.createColor(colorRequest));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update color by id")
    public ResponseEntity<ColorResponse> updateColor(@PathVariable Long id, @Valid @RequestBody ColorRequest colorRequest) {
        return ResponseEntity.ok(colorService.updateColor(id, colorRequest));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete color by id")
    public ResponseEntity<Void> deleteColor(@PathVariable Long id) {
        colorService.deleteColor(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk-delete")
    @Operation(summary = "Delete multiple colors")
    public ResponseEntity<Void> deleteColors(@Valid @RequestBody BulkDeleteRequest request) {
        colorService.deleteColors(request.getIds());
        return ResponseEntity.noContent().build();
    }
}
