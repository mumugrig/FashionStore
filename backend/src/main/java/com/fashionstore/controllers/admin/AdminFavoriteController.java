package com.fashionstore.controllers.admin;

import com.fashionstore.dto.request.BulkDeleteRequest;
import com.fashionstore.dto.request.FavoriteRequest;
import com.fashionstore.dto.response.AdminFavoriteResponse;
import com.fashionstore.dto.response.FavoriteResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.services.FavoriteService;
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
@Tag(name = "Admin Favorites", description = "Admin favorite management")
public class AdminFavoriteController {
    private final FavoriteService favoriteService;

    @GetMapping("/favorites")
    @Operation(summary = "Get paged favorites with admin details")
    public ResponseEntity<PageResponse<AdminFavoriteResponse>> getPagedFavorites(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String filterColumn,
            @RequestParam(required = false) String filterValue) {
        return ResponseEntity.ok(favoriteService.getPagedAdminFavorites(page, size, search, filterColumn, filterValue));
    }

    @GetMapping("/users/{userId}/favorites")
    @Operation(summary = "Get paged favorites for a user")
    public ResponseEntity<PageResponse<FavoriteResponse>> getPagedFavoritesByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(favoriteService.getPagedFavoritesByUserId(userId, page, size));
    }

    @GetMapping("/favorites/{id}")
    @Operation(summary = "Get favorite by id")
    public ResponseEntity<AdminFavoriteResponse> getFavoriteById(@PathVariable Long id) {
        return ResponseEntity.ok(favoriteService.getAdminFavoriteById(id));
    }

    @PostMapping("/favorites")
    @Operation(summary = "Create favorite")
    public ResponseEntity<FavoriteResponse> addFavorite(@Valid @RequestBody FavoriteRequest favoriteRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(favoriteService.addFavorite(favoriteRequest));
    }

    @PutMapping("/favorites/{id}")
    @Operation(summary = "Update favorite by id")
    public ResponseEntity<FavoriteResponse> updateFavorite(@PathVariable Long id, @Valid @RequestBody FavoriteRequest favoriteRequest) {
        return ResponseEntity.ok(favoriteService.updateFavorite(id, favoriteRequest));
    }

    @DeleteMapping("/favorites/{id}")
    @Operation(summary = "Delete favorite by id")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long id) {
        favoriteService.deleteFavorite(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/favorites/bulk-delete")
    @Operation(summary = "Delete multiple favorites")
    public ResponseEntity<Void> removeFavorites(@Valid @RequestBody BulkDeleteRequest request) {
        favoriteService.deleteFavorites(request.getIds());
        return ResponseEntity.noContent().build();
    }
}
