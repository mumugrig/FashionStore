package com.fashionstore.controllers;

import com.fashionstore.dto.request.FavoriteRequest;
import com.fashionstore.dto.response.FavoriteResponse;
import com.fashionstore.services.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FavoriteResponse>> getFavoritesByUserId(@PathVariable Long userId) {
        List<FavoriteResponse> favoriteResponses = favoriteService.getFavoriteByUserId(userId);
        return ResponseEntity.ok(favoriteResponses);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FavoriteResponse> addFavorite(@Valid @RequestBody FavoriteRequest favoriteRequest) {
        FavoriteResponse favoriteResponse = favoriteService.addFavorite(favoriteRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(favoriteResponse);
    }

    @DeleteMapping("/items/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long id){
        favoriteService.deleteFavorite(id);
        return ResponseEntity.noContent().build();
    }
}
