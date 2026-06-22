package com.fashionstore.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ColorRequest {
    @NotBlank(message = "Color name is required")
    @Size(min = 1, max = 50, message = "Color name must be between 1 and 50 characters")
    private String name;

    @NotBlank(message = "Color value is required")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Color value must be a valid hex color code")
    private String value;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;
}

