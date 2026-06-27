package com.fashionstore.dto.response;

import com.fashionstore.models.ItemVariant;
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
    private String sizeLabel;
    private String sizeSystem;
    private String colorName;
    private String colorValue;
    private boolean variantActive;
    private int variantStockLeft;
    private String variantImageUrl;

    public AdminReviewResponse(ReviewResponse base) {
        setId(base.getId());
        setBody(base.getBody());
        setSizeFit(base.getSizeFit());
        setQuality(base.getQuality());
        setComfort(base.getComfort());
        setUserId(base.getUserId());
        setItemVariantId(base.getItemVariantId());
    }

    public static AdminReviewResponse from(Review review) {
        AdminReviewResponse result = new AdminReviewResponse(ReviewResponse.from(review));
        result.userFirstName = review.getUser().getFirstName();
        result.userLastName = review.getUser().getLastName();
        result.userName = fullName(result.userFirstName, result.userLastName);
        result.userEmail = review.getUser().getEmail();
        applyVariant(result, review.getItemVariant());
        return result;
    }

    private static void applyVariant(AdminReviewResponse result, ItemVariant variant) {
        result.itemId = variant.getItem().getId();
        result.itemName = variant.getItem().getName();
        result.sizeLabel = variant.getSize().getLabel();
        result.sizeSystem = variant.getSize().getSizeSystem().name();
        result.colorName = variant.getColor().getName();
        result.colorValue = variant.getColor().getValue();
        result.variantActive = variant.isActive();
        result.variantStockLeft = variant.getStockLeft();
        result.variantImageUrl = variant.getImageUrl();
    }

    private static String fullName(String firstName, String lastName) {
        return ((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName)).trim();
    }
}
