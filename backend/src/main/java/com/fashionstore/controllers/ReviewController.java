package com.fashionstore.controllers;

import com.fashionstore.dto.request.ReviewRequest;
import com.fashionstore.dto.response.ReviewResponse;
import com.fashionstore.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items/{itemId}/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@RequestBody ReviewRequest reviewRequest) {
        ReviewResponse reviewResponse = reviewService.createReview(reviewRequest);
        return ResponseEntity.ok(reviewResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> updateReview(@RequestBody ReviewRequest reviewRequest, @PathVariable Long id) {
        ReviewResponse reviewResponse = reviewService.updateReview(id, reviewRequest);
        return ResponseEntity.ok(reviewResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id){
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
