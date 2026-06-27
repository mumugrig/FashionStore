package com.fashionstore.dto.response;

import com.fashionstore.models.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminItemResponse extends ItemResponse {
    private String categoryName;
    private int variantCount;

    public AdminItemResponse(ItemResponse base) {
        setId(base.getId());
        setName(base.getName());
        setPrice(base.getPrice());
        setDescription(base.getDescription());
        setImageUrl(base.getImageUrl());
        setAudience(base.getAudience());
        setCategoryId(base.getCategoryId());
        setVariants(base.getVariants());
    }

    public static AdminItemResponse from(Item item) {
        AdminItemResponse result = new AdminItemResponse(ItemResponse.from(item));
        result.categoryName = item.getCategory().getName();
        result.variantCount = item.getVariants() == null ? 0 : item.getVariants().size();
        return result;
    }
}
