package com.fashionstore.controllers.user;

import com.fashionstore.dto.request.ReviewRequest;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.dto.response.ReviewResponse;
import com.fashionstore.services.ReviewService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items/{itemId}/reviews")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<PageResponse<ReviewResponse>> getPagedReviews(
            @PathVariable Long itemId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(reviewService.getPagedReviewsByItem(itemId, page, size));
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(Authentication authentication, @Valid @RequestBody ReviewRequest reviewRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(authentication, reviewRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> updateReview(Authentication authentication, @Valid @RequestBody ReviewRequest reviewRequest, @PathVariable Long id) {
        return ResponseEntity.ok(reviewService.updateReview(authentication, id, reviewRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(Authentication authentication, @PathVariable Long id) {
        reviewService.deleteReview(authentication, id);
        return ResponseEntity.noContent().build();
    }
}
