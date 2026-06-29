package com.fashionstore.services;

import com.fashionstore.dto.request.ReviewRequest;
import com.fashionstore.dto.response.AdminReviewResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.dto.response.ReviewResponse;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.models.Review;
import com.fashionstore.repositories.ItemRepository;
import com.fashionstore.repositories.ReviewRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ItemRepository itemRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public ReviewResponse createReview(Authentication authentication, Long itemId, ReviewRequest reviewRequest) {
        Review review = new Review();
        applyReviewRequest(review, reviewRequest, itemId);
        review.setUser(currentUserService.findCurrentUser(authentication));
        Review savedReview = reviewRepository.save(review);
        return ReviewResponse.from(savedReview);
    }

    @Transactional
    public ReviewResponse updateReview(Authentication authentication, Long itemId, Long id, ReviewRequest reviewRequest){
        var currentUser = currentUserService.findCurrentUser(authentication);
        Review review = reviewRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new NotFoundException("Review", id));

        applyReviewRequest(review, reviewRequest, itemId);

        Review updatedReview = reviewRepository.save(review);
        return ReviewResponse.from(updatedReview);
    }

    @Transactional
    public ReviewResponse updateReview(Long id, ReviewRequest reviewRequest){
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Review", id));

        applyReviewRequest(review, reviewRequest, null);

        Review updatedReview = reviewRepository.save(review);
        return ReviewResponse.from(updatedReview);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(Long id) {
        return reviewRepository.findById(id)
                .map(ReviewResponse::from)
                .orElseThrow(() -> new NotFoundException("Review", id));
    }

    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getPagedReviews(int page, int size) {
        return PageResponse.from(reviewRepository.findAll(PageRequestFactory.create(page, size)), ReviewResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getPagedReviews(int page, int size, String search, String filterColumn, String filterValue) {
        if (!AdminFilterSpecification.hasFilters(search, filterColumn, filterValue)) {
            return getPagedReviews(page, size);
        }
        return PageResponse.from(reviewRepository.findAll(
                AdminFilterSpecification.create(AdminSearchFields.REVIEWS, search, filterColumn, filterValue),
                PageRequestFactory.create(page, size)
        ), ReviewResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminReviewResponse> getPagedAdminReviews(int page, int size, String search, String filterColumn, String filterValue) {
        if (!AdminFilterSpecification.hasFilters(search, filterColumn, filterValue)) {
            return PageResponse.from(reviewRepository.findAll(PageRequestFactory.create(page, size)), AdminReviewResponse::from);
        }
        return PageResponse.from(reviewRepository.findAll(
                AdminFilterSpecification.create(AdminSearchFields.REVIEWS, search, filterColumn, filterValue),
                PageRequestFactory.create(page, size)
        ), AdminReviewResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getPagedReviewsByItem(Long itemId, int page, int size) {
        return PageResponse.from(reviewRepository.findByItemId(itemId, PageRequestFactory.create(page, size)), ReviewResponse::from);
    }

    @Transactional
    public void deleteReview(Authentication authentication, Long id){
        var currentUser = currentUserService.findCurrentUser(authentication);
        Review review = reviewRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new NotFoundException("Review", id));
        reviewRepository.delete(review);
    }

    @Transactional
    public void deleteReview(Long id){
        if (!reviewRepository.existsById(id)) {
            throw new NotFoundException("Review", id);
        }
        reviewRepository.deleteById(id);
    }

    @Transactional
    public void deleteReviews(List<Long> ids) {
        ids.forEach(this::deleteReview);
    }

    private void applyReviewRequest(Review review, ReviewRequest reviewRequest, Long routeItemId) {
        review.setBody(reviewRequest.getBody());
        review.setComfort(reviewRequest.getComfort());
        review.setQuality(reviewRequest.getQuality());
        review.setSizeFit(reviewRequest.getSizeFit());
        Long itemId = routeItemId != null ? routeItemId : reviewRequest.getItemId();
        if (itemId == null) {
            throw new ValidationException("Item ID is required");
        }
        review.setItem(itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item", itemId)));
    }
}
