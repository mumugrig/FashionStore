package com.fashionstore.dto.response;

import com.fashionstore.models.CartItem;
import com.fashionstore.models.ItemVariant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminCartItemResponse extends CartItemResponse {
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

    public AdminCartItemResponse(CartItemResponse base) {
        setId(base.getId());
        setQuantity(base.getQuantity());
        setItemVariantId(base.getItemVariantId());
        setUserId(base.getUserId());
    }

    public static AdminCartItemResponse from(CartItem cartItem) {
        AdminCartItemResponse result = new AdminCartItemResponse(CartItemResponse.from(cartItem));
        result.userFirstName = cartItem.getUser().getFirstName();
        result.userLastName = cartItem.getUser().getLastName();
        result.userName = fullName(result.userFirstName, result.userLastName);
        result.userEmail = cartItem.getUser().getEmail();
        applyVariant(result, cartItem.getItemVariant());
        return result;
    }

    private static void applyVariant(AdminCartItemResponse result, ItemVariant variant) {
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
