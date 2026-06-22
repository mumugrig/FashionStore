package com.fashionstore.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;

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
    private float price;

    @NotBlank(message = "Item description is required")
    @Size(min = 10, max = 5000, message = "Item description must be between 10 and 5000 characters")
    private String description;

    @NotBlank(message = "Audience is required")
    private String audience;

    @NotNull(message = "Category ID is required")
    private Long categoryId;
}

