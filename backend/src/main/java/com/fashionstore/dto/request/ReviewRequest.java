package com.fashionstore.dto.request;

import com.fashionstore.vo.Comfort;
import com.fashionstore.vo.Quality;
import com.fashionstore.vo.SizeFit;
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

    @NotNull(message = "Size fit is required")
    private SizeFit sizeFit;

    @NotNull(message = "Quality is required")
    private Quality quality;

    @NotNull(message = "Comfort is required")
    private Comfort comfort;

    private Long itemId;
}
