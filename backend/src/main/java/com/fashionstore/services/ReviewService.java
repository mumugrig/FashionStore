package com.fashionstore.services;

import com.fashionstore.dto.request.ReviewRequest;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.dto.response.ReviewResponse;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.models.Review;
import com.fashionstore.repositories.ItemVariantRepository;
import com.fashionstore.repositories.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ItemVariantRepository itemVariantRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public ReviewResponse createReview(Authentication authentication, ReviewRequest reviewRequest) {
        Review review = new Review();
        applyReviewRequest(review, reviewRequest);
        review.setUser(currentUserService.findCurrentUser(authentication));
        Review savedReview = reviewRepository.save(review);
        return ReviewResponse.from(savedReview);
    }

    @Transactional
    public ReviewResponse updateReview(Authentication authentication, Long id, ReviewRequest reviewRequest){
        var currentUser = currentUserService.findCurrentUser(authentication);
        Review review = reviewRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new NotFoundException("Review", id));

        applyReviewRequest(review, reviewRequest);

        Review updatedReview = reviewRepository.save(review);
        return ReviewResponse.from(updatedReview);
    }

    @Transactional
    public ReviewResponse updateReview(Long id, ReviewRequest reviewRequest){
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Review", id));

        applyReviewRequest(review, reviewRequest);

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
    public PageResponse<ReviewResponse> getPagedReviewsByItem(Long itemId, int page, int size) {
        return PageResponse.from(reviewRepository.findByItemVariantItemId(itemId, PageRequestFactory.create(page, size)), ReviewResponse::from);
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

    private void applyReviewRequest(Review review, ReviewRequest reviewRequest) {
        review.setBody(reviewRequest.getBody());
        review.setComfort(reviewRequest.getComfort());
        review.setQuality(reviewRequest.getQuality());
        review.setSizeFit(reviewRequest.getSizeFit());
        review.setItemVariant(itemVariantRepository.findById(reviewRequest.getItemVariantId())
                .orElseThrow(() -> new NotFoundException("ItemVariant", reviewRequest.getItemVariantId())));
    }
}
