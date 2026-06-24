package com.fashionstore.services;

import com.fashionstore.dto.request.ReviewRequest;
import com.fashionstore.dto.response.ReviewResponse;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.models.Review;
import com.fashionstore.repositories.ItemVariantRepository;
import com.fashionstore.repositories.ReviewRepository;
import com.fashionstore.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ItemVariantRepository itemVariantRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, ItemVariantRepository itemVariantRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.itemVariantRepository = itemVariantRepository;
    }

    @Transactional
    public ReviewResponse createReview(ReviewRequest reviewRequest) {
        Review review = new Review();
        review.setBody(reviewRequest.getBody());
        review.setComfort(reviewRequest.getComfort());
        review.setQuality(reviewRequest.getQuality());
        review.setSizeFit(reviewRequest.getSizeFit());
        review.setUser(userRepository.findById(reviewRequest.getUserId())
                .orElseThrow(() -> new NotFoundException("User", reviewRequest.getUserId())));
        review.setItemVariant(itemVariantRepository.findById(reviewRequest.getItemVariantId())
                .orElseThrow(() -> new NotFoundException("ItemVariant", reviewRequest.getItemVariantId())));
        Review savedReview = reviewRepository.save(review);
        return ReviewResponse.from(savedReview);
    }

    @Transactional
    public ReviewResponse updateReview(Long id, ReviewRequest reviewRequest){
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            review.setBody(reviewRequest.getBody());
            review.setComfort(reviewRequest.getComfort());
            review.setQuality(reviewRequest.getQuality());
            review.setSizeFit(reviewRequest.getSizeFit());
            review.setUser(userRepository.findById(reviewRequest.getUserId())
                    .orElseThrow(() -> new NotFoundException("User", reviewRequest.getUserId())));
            review.setItemVariant(itemVariantRepository.findById(reviewRequest.getItemVariantId())
                    .orElseThrow(() -> new NotFoundException("ItemVariant", reviewRequest.getItemVariantId())));
            Review updatedReview = reviewRepository.save(review);
            return ReviewResponse.from(updatedReview);
        }
        throw new NotFoundException("Review", id);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(Long id) {
        return reviewRepository.findById(id)
                .map(ReviewResponse::from)
                .orElseThrow(() -> new NotFoundException("Review", id));
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(ReviewResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteReview(Long id){
        if (!reviewRepository.existsById(id)) {
            throw new NotFoundException("Review", id);
        }
        reviewRepository.deleteById(id);
    }
}
