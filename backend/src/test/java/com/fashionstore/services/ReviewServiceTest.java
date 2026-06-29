package com.fashionstore.services;

import com.fashionstore.dto.response.ReviewResponse;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.repositories.ItemRepository;
import com.fashionstore.repositories.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

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
    @Mock private CurrentUserService currentUserServiceMock;
    @Mock private ReviewRepository reviewRepositoryMock;
    @Mock private ItemRepository itemRepositoryMock;
    private ReviewService objectUnderTest;

    @BeforeEach
    void setUp() {
        objectUnderTest = new ReviewService(reviewRepositoryMock, itemRepositoryMock, currentUserServiceMock);
    }

    @Test
    void createReview_whenUserAndItemExist_returnsCreatedReview() {
        var user = user(1L, "review-service@example.com");
        var category = category(1L, "Shirts");
        var item = item(1L, "Review Shirt", category);
        var createdReview = review(1L, user, item, "Original review body");

        var authentication = authentication(user.getId());
        when(currentUserServiceMock.findCurrentUser(authentication)).thenReturn(user);
        when(itemRepositoryMock.findById(item.getId())).thenReturn(Optional.of(item));
        when(reviewRepositoryMock.save(any())).thenReturn(createdReview);

        ReviewResponse response = objectUnderTest.createReview(authentication, item.getId(), reviewRequest(user.getId(), item.getId(), "Original review body"));

        assertEquals("Original review body", response.getBody(), "Created review body should match saved entity");
        assertEquals(item.getId(), response.getItemId(), "Created review should reference the route item");
    }

    @Test
    void updateReview_whenReviewExists_returnsUpdatedReview() {
        var user = user(1L, "review-service@example.com");
        var category = category(1L, "Shirts");
        var item = item(1L, "Review Shirt", category);
        var createdReview = review(1L, user, item, "Original review body");
        var updatedReview = review(1L, user, item, "Updated review body");

        var authentication = authentication(user.getId());
        when(currentUserServiceMock.findCurrentUser(authentication)).thenReturn(user);
        when(reviewRepositoryMock.findByIdAndUserId(1L, user.getId())).thenReturn(Optional.of(createdReview));
        when(itemRepositoryMock.findById(item.getId())).thenReturn(Optional.of(item));
        when(reviewRepositoryMock.save(createdReview)).thenReturn(updatedReview);

        ReviewResponse response = objectUnderTest.updateReview(authentication, item.getId(), 1L, reviewRequest(user.getId(), item.getId(), "Updated review body"));

        assertEquals("Updated review body", response.getBody(), "Updated review body should match saved entity");
    }

    @Test
    void updateReview_whenAuthenticatedUserDoesNotOwnReview_throwsNotFoundException() {
        var user = user(1L, "review-owner@example.com");
        var authentication = authentication(user.getId());
        when(currentUserServiceMock.findCurrentUser(authentication)).thenReturn(user);
        when(reviewRepositoryMock.findByIdAndUserId(1L, user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> objectUnderTest.updateReview(authentication, 2L, 1L, reviewRequest(99L, 2L, "Updated review body")));
    }

    @Test
    void updateReview_whenCalledByAdmin_updatesAnyReview() {
        var user = user(1L, "review-admin-target@example.com");
        var category = category(1L, "Shirts");
        var item = item(1L, "Review Shirt", category);
        var createdReview = review(1L, user, item, "Original review body");
        var updatedReview = review(1L, user, item, "Admin updated review body");

        when(reviewRepositoryMock.findById(1L)).thenReturn(Optional.of(createdReview));
        when(itemRepositoryMock.findById(item.getId())).thenReturn(Optional.of(item));
        when(reviewRepositoryMock.save(createdReview)).thenReturn(updatedReview);

        ReviewResponse response = objectUnderTest.updateReview(1L, reviewRequest(99L, item.getId(), "Admin updated review body"));

        assertEquals("Admin updated review body", response.getBody(), "Admin update should not require ownership");
    }

    @Test
    void getPagedReviews_whenReviewsExist_returnsPagedReviews() {
        var user = user(1L, "review-service@example.com");
        var category = category(1L, "Shirts");
        var item = item(1L, "Review Shirt", category);
        var review = review(1L, user, item, "Review body");

        when(reviewRepositoryMock.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(review)));

        var response = objectUnderTest.getPagedReviews(1, 20);

        assertFalse(response.getContent().isEmpty(), "Review page should contain repository results");
    }

    @Test
    void deleteReview_whenReviewExists_deletesReview() {
        var user = user(1L, "review-service@example.com");
        var category = category(1L, "Shirts");
        var item = item(1L, "Review Shirt", category);
        var review = review(1L, user, item, "Review body");
        var authentication = authentication(user.getId());

        when(currentUserServiceMock.findCurrentUser(authentication)).thenReturn(user);
        when(reviewRepositoryMock.findByIdAndUserId(1L, user.getId())).thenReturn(Optional.of(review));

        objectUnderTest.deleteReview(authentication, 1L);

        verify(reviewRepositoryMock).delete(review);
    }

    @Test
    void deleteReview_whenAuthenticatedUserDoesNotOwnReview_throwsNotFoundException() {
        var user = user(1L, "review-owner-delete@example.com");
        var authentication = authentication(user.getId());
        when(currentUserServiceMock.findCurrentUser(authentication)).thenReturn(user);
        when(reviewRepositoryMock.findByIdAndUserId(1L, user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> objectUnderTest.deleteReview(authentication, 1L));
    }

    @Test
    void deleteReview_whenCalledByAdmin_deletesAnyReview() {
        when(reviewRepositoryMock.existsById(1L)).thenReturn(true);

        objectUnderTest.deleteReview(1L);

        verify(reviewRepositoryMock).deleteById(1L);
    }

    @Test
    void getReviewById_whenReviewIsMissing_throwsNotFoundException() {
        when(reviewRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> objectUnderTest.getReviewById(1L));
    }

    private Authentication authentication(Long userId) {
        return new TestingAuthenticationToken("user-" + userId + "@example.com", null);
    }
}
