package com.fashionstore.dto.response;

import com.fashionstore.models.ItemVariant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class AdminItemVariantResponse extends ItemVariantResponse {
    private String itemName;
    private BigDecimal itemPrice;
    private String itemAudience;
    private String sizeLabel;
    private String sizeSystem;
    private String colorName;
    private String colorValue;
    private String colorImageUrl;

    public AdminItemVariantResponse(ItemVariantResponse base) {
        setId(base.getId());
        setActive(base.isActive());
        setStockLeft(base.getStockLeft());
        setImageUrl(base.getImageUrl());
        setItemId(base.getItemId());
        setSizeId(base.getSizeId());
        setColorId(base.getColorId());
    }

    public static AdminItemVariantResponse from(ItemVariant itemVariant) {
        AdminItemVariantResponse result = new AdminItemVariantResponse(ItemVariantResponse.from(itemVariant));
        result.itemName = itemVariant.getItem().getName();
        result.itemPrice = itemVariant.getItem().getPrice();
        result.itemAudience = itemVariant.getItem().getAudience().name();
        result.sizeLabel = itemVariant.getSize().getLabel();
        result.sizeSystem = itemVariant.getSize().getSizeSystem().name();
        result.colorName = itemVariant.getColor().getName();
        result.colorValue = itemVariant.getColor().getValue();
        result.colorImageUrl = itemVariant.getColor().getImageUrl();
        return result;
    }
}
