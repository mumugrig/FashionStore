package com.fashionstore.controllers.user;

import com.fashionstore.dto.request.ReviewRequest;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.dto.response.ReviewResponse;
import com.fashionstore.services.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Reviews", description = "Product reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    @Operation(summary = "Get paged reviews for an item")
    public ResponseEntity<PageResponse<ReviewResponse>> getPagedReviews(
            @PathVariable Long itemId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(reviewService.getPagedReviewsByItem(itemId, page, size));
    }

    @PostMapping
    @Operation(summary = "Create review for an item variant")
    public ResponseEntity<ReviewResponse> createReview(Authentication authentication, @Valid @RequestBody ReviewRequest reviewRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(authentication, reviewRequest));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update current user's review")
    public ResponseEntity<ReviewResponse> updateReview(Authentication authentication, @Valid @RequestBody ReviewRequest reviewRequest, @PathVariable Long id) {
        return ResponseEntity.ok(reviewService.updateReview(authentication, id, reviewRequest));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete current user's review")
    public ResponseEntity<Void> deleteReview(Authentication authentication, @PathVariable Long id) {
        reviewService.deleteReview(authentication, id);
        return ResponseEntity.noContent().build();
    }
}
