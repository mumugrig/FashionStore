package com.fashionstore.dto.request;

import com.fashionstore.vo.SizeSystem;
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
public class SizeRequest {
    @NotBlank(message = "Size label is required")
    @Size(min = 1, max = 50, message = "Size label must be between 1 and 50 characters")
    private String label;

    @NotNull(message = "Size system is required")
    private SizeSystem sizeSystem;
}

