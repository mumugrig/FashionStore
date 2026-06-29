package com.fashionstore.dto.response;

import com.fashionstore.models.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminReviewResponse extends ReviewResponse {
    private String userFirstName;
    private String userLastName;
    private String userName;
    private String userEmail;
    private Long itemId;
    private String itemName;

    public AdminReviewResponse(ReviewResponse base) {
        setId(base.getId());
        setBody(base.getBody());
        setSizeFit(base.getSizeFit());
        setQuality(base.getQuality());
        setComfort(base.getComfort());
        setUserId(base.getUserId());
        setItemId(base.getItemId());
    }

    public static AdminReviewResponse from(Review review) {
        AdminReviewResponse result = new AdminReviewResponse(ReviewResponse.from(review));
        result.userFirstName = review.getUser().getFirstName();
        result.userLastName = review.getUser().getLastName();
        result.userName = fullName(result.userFirstName, result.userLastName);
        result.userEmail = review.getUser().getEmail();
        result.itemId = review.getItem().getId();
        result.itemName = review.getItem().getName();
        return result;
    }

    private static String fullName(String firstName, String lastName) {
        return ((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName)).trim();
    }
}
