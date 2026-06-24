package com.fashionstore.controllers;

import com.fashionstore.services.ReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest extends ControllerTestSupport {
    @Mock private ReviewService reviewService;
    @InjectMocks private ReviewController reviewController;

    @Test
    void createsUpdatesListsAndDeletesReviews() {
        when(reviewService.createReview(any())).thenReturn(reviewResponse(1L, 1L, 2L, "Comfortable enough for daily wear."));
        when(reviewService.updateReview(org.mockito.ArgumentMatchers.eq(1L), any()))
                .thenReturn(reviewResponse(1L, 1L, 2L, "Updated review body with enough text."));
        when(reviewService.getAllReviews()).thenReturn(List.of(reviewResponse(1L, 1L, 2L, "Updated review body with enough text.")));

        var created = reviewController.createReview(reviewRequest(1L, 2L, "Comfortable enough for daily wear."));
        var updated = reviewController.updateReview(reviewRequest(1L, 2L, "Updated review body with enough text."), 1L);
        var listed = reviewController.getAllReviews();
        var deleted = reviewController.deleteReview(1L);

        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        assertEquals("Updated review body with enough text.", updated.getBody().getBody());
        assertEquals(1, listed.getBody().size());
        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());
        verify(reviewService).deleteReview(1L);
    }
}
