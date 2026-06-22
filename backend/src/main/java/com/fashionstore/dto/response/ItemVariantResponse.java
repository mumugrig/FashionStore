package com.fashionstore.dto.response;

import com.fashionstore.models.ItemVariant;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemVariantResponse {
    private Long id;
    private boolean isActive;
    private int stockLeft;
    private Long itemId;
    private Long sizeId;
    private Long colorId;

    public static ItemVariantResponse from(ItemVariant itemVariant){
        ItemVariantResponse result = new ItemVariantResponse();
        result.id = itemVariant.getId();
        result.isActive = itemVariant.isActive();
        result.stockLeft = itemVariant.getStockLeft();
        result.itemId = itemVariant.getItem().getId();
        result.sizeId = itemVariant.getSize().getId();
        result.colorId = itemVariant.getColor().getId();
        return result;
    }
}

