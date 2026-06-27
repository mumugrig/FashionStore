package com.fashionstore.controllers.user;

import com.fashionstore.dto.request.FavoriteRequest;
import com.fashionstore.dto.response.FavoriteResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.services.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class FavoriteController {
    private final FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<PageResponse<FavoriteResponse>> getPagedFavorites(
            Authentication authentication,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(favoriteService.getPagedFavorites(authentication, page, size, search));
    }

    @PostMapping("/items")
    public ResponseEntity<FavoriteResponse> addFavorite(Authentication authentication, @Valid @RequestBody FavoriteRequest favoriteRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(favoriteService.addFavorite(authentication, favoriteRequest));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> removeFavorite(Authentication authentication, @PathVariable Long id) {
        favoriteService.deleteFavorite(authentication, id);
        return ResponseEntity.noContent().build();
    }
}
