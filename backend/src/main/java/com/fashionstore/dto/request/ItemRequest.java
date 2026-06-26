package com.fashionstore.dto.request;

import com.fashionstore.vo.Audience;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    @NotBlank(message = "Item name is required")
    @Size(min = 1, max = 255, message = "Item name must be between 1 and 255 characters")
    private String name;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Price cannot exceed 999999.99")
    private BigDecimal price;

    @NotBlank(message = "Item description is required")
    @Size(min = 10, max = 5000, message = "Item description must be between 10 and 5000 characters")
    private String description;

    @Size(max = 2048, message = "Image URL cannot exceed 2048 characters")
    private String imageUrl;

    @NotNull(message = "Audience is required")
    private Audience audience;

    @NotNull(message = "Category ID is required")
    private Long categoryId;
}

