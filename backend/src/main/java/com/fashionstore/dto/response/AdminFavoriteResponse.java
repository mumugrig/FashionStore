package com.fashionstore.dto.response;

import com.fashionstore.models.Favorite;
import com.fashionstore.models.ItemVariant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminFavoriteResponse extends FavoriteResponse {
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

    public AdminFavoriteResponse(FavoriteResponse base) {
        setId(base.getId());
        setItemVariantId(base.getItemVariantId());
        setUserId(base.getUserId());
    }

    public static AdminFavoriteResponse from(Favorite favorite) {
        AdminFavoriteResponse result = new AdminFavoriteResponse(FavoriteResponse.from(favorite));
        result.userFirstName = favorite.getUser().getFirstName();
        result.userLastName = favorite.getUser().getLastName();
        result.userName = fullName(result.userFirstName, result.userLastName);
        result.userEmail = favorite.getUser().getEmail();
        applyVariant(result, favorite.getItemVariant());
        return result;
    }

    private static void applyVariant(AdminFavoriteResponse result, ItemVariant variant) {
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
