package com.fashionstore.services;

import com.fashionstore.dto.request.ReviewRequest;
import com.fashionstore.dto.response.ReviewResponse;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.models.Review;
import com.fashionstore.repositories.ItemVariantRepository;
import com.fashionstore.repositories.ReviewRepository;
import com.fashionstore.repositories.UserRepository;
import com.fashionstore.vo.Comfort;
import com.fashionstore.vo.Quality;
import com.fashionstore.vo.SizeFit;
import org.hibernate.annotations.SecondaryRow;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private ReviewRepository reviewRepository;
    private UserRepository userRepository;
    private ItemVariantRepository itemVariantRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, ItemVariantRepository itemVariantRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.itemVariantRepository = itemVariantRepository;
    }

    public ReviewResponse createReview(ReviewRequest reviewRequest) {
        Review review = new Review();
        review.setBody(reviewRequest.getBody());
        review.setComfort(Comfort.valueOf(reviewRequest.getComfort()));
        review.setQuality(Quality.valueOf(reviewRequest.getQuality()));
        review.setSizeFit(SizeFit.valueOf(reviewRequest.getSizeFit()));
        review.setUser(userRepository.findById(reviewRequest.getUserId()).orElse(null));
        review.setItemVariant(itemVariantRepository.findById(reviewRequest.getItemVariantId()).orElse(null));
        Review savedReview = reviewRepository.save(review);
        return ReviewResponse.from(savedReview);
    }

    public ReviewResponse updateReview(Long id, ReviewRequest reviewRequest){
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            review.setBody(reviewRequest.getBody());
            review.setComfort(Comfort.valueOf(reviewRequest.getComfort()));
            review.setQuality(Quality.valueOf(reviewRequest.getQuality()));
            review.setSizeFit(SizeFit.valueOf(reviewRequest.getSizeFit()));
            review.setUser(userRepository.findById(reviewRequest.getUserId()).orElse(null));
            review.setItemVariant(itemVariantRepository.findById(reviewRequest.getItemVariantId()).orElse(null));
            Review updatedReview = reviewRepository.save(review);
            return ReviewResponse.from(updatedReview);
        }
        throw new NotFoundException("Review", id);
    }

    public ReviewResponse getReviewById(Long id) {
        return reviewRepository.findById(id)
                .map(ReviewResponse::from)
                .orElseThrow(() -> new NotFoundException("Review", id));
    }

    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(ReviewResponse::from)
                .collect(Collectors.toList());
    }

    public void deleteReview(Long id){
        reviewRepository.deleteById(id);
    }
}
