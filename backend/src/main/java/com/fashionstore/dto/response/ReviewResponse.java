package com.fashionstore.dto.response;

import com.fashionstore.models.Review;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private String body;
    private String sizeFit;
    private String quality;
    private String comfort;
    private Long userId;
    private Long itemVariantId;

    public static ReviewResponse from(Review review){
        ReviewResponse result = new ReviewResponse();
        result.id = review.getId();
        result.body = review.getBody();
        result.sizeFit = review.getSizeFit().name();
        result.quality = review.getQuality().name();
        result.comfort = review.getComfort().name();
        result.userId = review.getUser().getId();
        result.itemVariantId = review.getItemVariant().getId();
        return result;
    }
}

