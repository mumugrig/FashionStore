package com.fashionstore.services;

import com.fashionstore.dto.response.ReviewResponse;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.repositories.ItemVariantRepository;
import com.fashionstore.repositories.ReviewRepository;
import com.fashionstore.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest extends ServiceTestSupport {
    @Mock private ReviewRepository reviewRepository;
    @Mock private UserRepository userRepository;
    @Mock private ItemVariantRepository itemVariantRepository;
    @InjectMocks private ReviewService reviewService;

    @Test
    void createsUpdatesQueriesAndDeletesReviews() {
        var user = user(1L, "review-service@example.com");
        var category = category(1L, "Shirts");
        var item = item(1L, "Review Shirt", category);
        var size = size(1L, "M");
        var color = color(1L, "Blue", "#0000ff");
        var variant = itemVariant(1L, item, size, color);
        var createdReview = review(1L, user, variant, "Original review body");
        var updatedReview = review(1L, user, variant, "Updated review body");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemVariantRepository.findById(variant.getId())).thenReturn(Optional.of(variant));
        when(reviewRepository.save(any())).thenReturn(createdReview);
        ReviewResponse created = reviewService.createReview(reviewRequest(user.getId(), variant.getId(), "Original review body"));

        when(reviewRepository.findById(created.getId())).thenReturn(Optional.of(createdReview));
        when(reviewRepository.save(createdReview)).thenReturn(updatedReview);
        ReviewResponse updated = reviewService.updateReview(created.getId(), reviewRequest(user.getId(), variant.getId(), "Updated review body"));

        when(reviewRepository.findAll()).thenReturn(List.of(updatedReview));
        assertEquals("Updated review body", updated.getBody());
        assertFalse(reviewService.getAllReviews().isEmpty());

        reviewService.deleteReview(created.getId());
        verify(reviewRepository).deleteById(created.getId());
    }

    @Test
    void throwsWhenReviewIsMissing() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reviewService.getReviewById(1L));
    }
}
