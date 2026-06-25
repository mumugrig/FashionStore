package com.fashionstore.controllers;

import com.fashionstore.services.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest extends ControllerTestSupport {
    @Mock private ReviewService reviewServiceMock;
    private com.fashionstore.controllers.user.ReviewController objectUnderTest;

    @BeforeEach
    void setUp() {
        objectUnderTest = new com.fashionstore.controllers.user.ReviewController(reviewServiceMock);
    }

    @Test
    void createReview_whenRequestIsValid_returnsCreatedReview() {
        Authentication authentication = authentication();
        when(reviewServiceMock.createReview(eq(authentication), any()))
                .thenReturn(reviewResponse(1L, 1L, 2L, "Comfortable enough for daily wear."));

        var response = objectUnderTest.createReview(authentication, reviewRequest(1L, 2L, "Comfortable enough for daily wear."));

        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Review creation should return HTTP 201");
        assertEquals("Comfortable enough for daily wear.", response.getBody().getBody(), "Created review body should match service response");
    }

    @Test
    void updateReview_whenReviewExists_returnsUpdatedReview() {
        Authentication authentication = authentication();
        when(reviewServiceMock.updateReview(eq(authentication), eq(1L), any()))
                .thenReturn(reviewResponse(1L, 1L, 2L, "Updated review body with enough text."));

        var response = objectUnderTest.updateReview(authentication, reviewRequest(1L, 2L, "Updated review body with enough text."), 1L);

        assertEquals("Updated review body with enough text.", response.getBody().getBody(), "Updated review body should match service response");
    }

    @Test
    void getPagedReviews_whenPageIsRequested_returnsPagedReviews() {
        when(reviewServiceMock.getPagedReviewsByItem(1L, 1, 20))
                .thenReturn(pageResponse(reviewResponse(1L, 1L, 2L, "Updated review body with enough text.")));

        var response = objectUnderTest.getPagedReviews(1L, 1, 20);

        assertEquals(1, response.getBody().getContent().size(), "Review page should contain service results");
    }

    @Test
    void deleteReview_whenReviewExists_returnsNoContent() {
        Authentication authentication = authentication();

        var response = objectUnderTest.deleteReview(authentication, 1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "Review deletion should return HTTP 204");
        verify(reviewServiceMock).deleteReview(authentication, 1L);
    }

    private Authentication authentication() {
        return new TestingAuthenticationToken("review@example.com", null);
    }
}
