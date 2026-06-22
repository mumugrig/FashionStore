package com.fashionstore.dto.response;

import com.fashionstore.models.CartItem;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private Long id;
    private int quantity;
    private Long itemVariantId;
    private Long userId;

    public static CartItemResponse from(CartItem cartItem) {
        CartItemResponse result = new CartItemResponse();
        result.id = cartItem.getId();
        result.quantity = cartItem.getQuantity();
        result.itemVariantId = cartItem.getItemVariant().getId();
        result.userId = cartItem.getUser().getId();
        return result;
    }
}
