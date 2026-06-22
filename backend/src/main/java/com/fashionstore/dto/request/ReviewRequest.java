package com.fashionstore.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    private String body;
    private String sizeFit;
    private String quality;
    private String comfort;
    private Long userId;
    private Long itemVariantId;
}

