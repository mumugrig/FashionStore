package com.fashionstore.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    @NotBlank(message = "Review body is required")
    @Size(min = 10, max = 5000, message = "Review body must be between 10 and 5000 characters")
    private String body;

    private String sizeFit;

    private String quality;

    private String comfort;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Item variant ID is required")
    private Long itemVariantId;
}

