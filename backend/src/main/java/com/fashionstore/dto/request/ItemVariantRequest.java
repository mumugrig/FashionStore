package com.fashionstore.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemVariantRequest {
    private boolean isActive;
    private int stockLeft;
    private Long itemId;
    private Long sizeId;
    private Long colorId;
}

