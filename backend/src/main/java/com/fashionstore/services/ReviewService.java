package com.fashionstore.services;

import com.fashionstore.dto.request.ReviewRequest;
import com.fashionstore.dto.response.AdminReviewResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.dto.response.ReviewResponse;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.models.Review;
import com.fashionstore.repositories.ItemVariantRepository;
import com.fashionstore.repositories.ReviewRepository;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
    public PageResponse<ReviewResponse> getPagedReviews(int page, int size, String search, String filterColumn, String filterValue) {
        if (!AdminFilterSpecification.hasFilters(search, filterColumn, filterValue)) {
            return getPagedReviews(page, size);
        }
        return PageResponse.from(reviewRepository.findAll(
                AdminFilterSpecification.create(adminFields(), search, filterColumn, filterValue),
                PageRequestFactory.create(page, size)
        ), ReviewResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminReviewResponse> getPagedAdminReviews(int page, int size, String search, String filterColumn, String filterValue) {
        if (!AdminFilterSpecification.hasFilters(search, filterColumn, filterValue)) {
            return PageResponse.from(reviewRepository.findAll(PageRequestFactory.create(page, size)), AdminReviewResponse::from);
        }
        return PageResponse.from(reviewRepository.findAll(
                AdminFilterSpecification.create(adminFields(), search, filterColumn, filterValue),
                PageRequestFactory.create(page, size)
        ), AdminReviewResponse::from);
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

    @Transactional
    public void deleteReviews(List<Long> ids) {
        ids.forEach(this::deleteReview);
    }

    private void applyReviewRequest(Review review, ReviewRequest reviewRequest) {
        review.setBody(reviewRequest.getBody());
        review.setComfort(reviewRequest.getComfort());
        review.setQuality(reviewRequest.getQuality());
        review.setSizeFit(reviewRequest.getSizeFit());
        review.setItemVariant(itemVariantRepository.findById(reviewRequest.getItemVariantId())
                .orElseThrow(() -> new NotFoundException("ItemVariant", reviewRequest.getItemVariantId())));
    }

    private Map<String, Function<Root<Review>, Expression<?>>> adminFields() {
        return Map.ofEntries(
                Map.entry("id", root -> root.get("id")),
                Map.entry("body", root -> root.get("body")),
                Map.entry("sizeFit", root -> root.get("sizeFit")),
                Map.entry("quality", root -> root.get("quality")),
                Map.entry("comfort", root -> root.get("comfort")),
                Map.entry("userId", root -> root.get("user").get("id")),
                Map.entry("userFirstName", root -> root.get("user").get("firstName")),
                Map.entry("userLastName", root -> root.get("user").get("lastName")),
                Map.entry("userName", root -> root.get("user").get("firstName")),
                Map.entry("userEmail", root -> root.get("user").get("email")),
                Map.entry("itemVariantId", root -> root.get("itemVariant").get("id")),
                Map.entry("itemId", root -> root.get("itemVariant").get("item").get("id")),
                Map.entry("itemName", root -> root.get("itemVariant").get("item").get("name")),
                Map.entry("sizeLabel", root -> root.get("itemVariant").get("size").get("label")),
                Map.entry("sizeSystem", root -> root.get("itemVariant").get("size").get("sizeSystem")),
                Map.entry("colorName", root -> root.get("itemVariant").get("color").get("name")),
                Map.entry("colorValue", root -> root.get("itemVariant").get("color").get("value")),
                Map.entry("variantActive", root -> root.get("itemVariant").get("isActive")),
                Map.entry("variantStockLeft", root -> root.get("itemVariant").get("stockLeft"))
        );
    }
}
