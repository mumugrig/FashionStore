package com.fashionstore.controllers.user;

import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.dto.response.SizeResponse;
import com.fashionstore.services.SizeService;
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

@RestController
@RequestMapping("/api/sizes")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Sizes", description = "Catalog size browsing")
public class SizeController {
    private final SizeService sizeService;

    @GetMapping
    @Operation(summary = "Get paged sizes")
    public ResponseEntity<PageResponse<SizeResponse>> getPagedSizes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(sizeService.getPagedSizes(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get size by id")
    public ResponseEntity<SizeResponse> getSizeById(@PathVariable Long id) {
        return ResponseEntity.ok(sizeService.getSizeById(id));
    }
}
