package com.fashionstore.dto.response;

import com.fashionstore.models.Item;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponse {
    private Long id;
    private String name;
    private float price;
    private String description;
    private String audience;
    private Long categoryId;

    public static ItemResponse from(Item item){
        ItemResponse result = new ItemResponse();
        result.id = item.getId();
        result.name = item.getName();
        result.price = item.getPrice();
        result.description = item.getDescription();
        result.audience = item.getAudience().name();
        result.categoryId = item.getCategory().getId();
        return result;
    }
}

