package com.fashionstore.controllers;

import com.fashionstore.dto.request.FavoriteRequest;
import com.fashionstore.dto.response.FavoriteResponse;
import com.fashionstore.services.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favourites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<FavoriteResponse>> getFavoritesByUserId(@PathVariable Long userId) {
        List<FavoriteResponse> favoriteResponses = favoriteService.getFavoriteByUserId(userId);
        return ResponseEntity.ok(favoriteResponses);
    }

    @PostMapping
    public ResponseEntity<FavoriteResponse> addFavorite(@RequestBody FavoriteRequest favoriteRequest) {
        FavoriteResponse favoriteResponse = favoriteService.addFavorite(favoriteRequest);
        return ResponseEntity.ok(favoriteResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long id){
        favoriteService.deleteFavorite(id);
        return ResponseEntity.noContent().build();
    }
}
