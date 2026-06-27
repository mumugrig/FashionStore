package com.fashionstore.controllers.admin;

import com.fashionstore.dto.request.BulkDeleteRequest;
import com.fashionstore.dto.request.ReviewRequest;
import com.fashionstore.dto.response.AdminReviewResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.dto.response.ReviewResponse;
import com.fashionstore.services.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/admin/reviews")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminReviewController {
    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<PageResponse<AdminReviewResponse>> getPagedReviews(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String filterColumn,
            @RequestParam(required = false) String filterValue) {
        return ResponseEntity.ok(reviewService.getPagedAdminReviews(page, size, search, filterColumn, filterValue));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable Long id, @Valid @RequestBody ReviewRequest reviewRequest) {
        return ResponseEntity.ok(reviewService.updateReview(id, reviewRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk-delete")
    public ResponseEntity<Void> deleteReviews(@Valid @RequestBody BulkDeleteRequest request) {
        reviewService.deleteReviews(request.getIds());
        return ResponseEntity.noContent().build();
    }
}
