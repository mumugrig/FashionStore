package com.fashionstore.dto.response;

import com.fashionstore.models.Item;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private String imageUrl;
    private String audience;
    private Long categoryId;
    private List<ItemVariantResponse> variants;

    public static ItemResponse from(Item item){
        ItemResponse result = new ItemResponse();
        result.id = item.getId();
        result.name = item.getName();
        result.price = item.getPrice();
        result.description = item.getDescription();
        result.imageUrl = item.getImageUrl();
        result.audience = item.getAudience().name();
        result.categoryId = item.getCategory().getId();
        result.variants = item.getVariants() == null
                ? Collections.emptyList()
                : item.getVariants().stream().map(ItemVariantResponse::from).collect(Collectors.toList());
        return result;
    }
}

