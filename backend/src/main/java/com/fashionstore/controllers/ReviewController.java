package com.fashionstore.controllers;

import com.fashionstore.dto.request.ReviewRequest;
import com.fashionstore.dto.response.ReviewResponse;
import com.fashionstore.services.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items/{itemId}/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest reviewRequest) {
        ReviewResponse reviewResponse = reviewService.createReview(reviewRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponse> updateReview(@Valid @RequestBody ReviewRequest reviewRequest, @PathVariable Long id) {
        ReviewResponse reviewResponse = reviewService.updateReview(id, reviewRequest);
        return ResponseEntity.ok(reviewResponse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id){
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
