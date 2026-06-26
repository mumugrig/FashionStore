package com.fashionstore.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemVariantRequest {
    private boolean isActive;

    @Min(value = 0, message = "Stock left cannot be negative")
    @Max(value = 999999, message = "Stock left cannot exceed 999999")
    private int stockLeft;

    @Size(max = 2048, message = "Image URL cannot exceed 2048 characters")
    private String imageUrl;

    @NotNull(message = "Item ID is required")
    private Long itemId;

    @NotNull(message = "Size ID is required")
    private Long sizeId;

    @NotNull(message = "Color ID is required")
    private Long colorId;
}

