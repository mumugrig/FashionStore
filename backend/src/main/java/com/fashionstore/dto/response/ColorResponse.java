package com.fashionstore.dto.response;

import com.fashionstore.models.Color;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ColorResponse {
    private Long id;
    private String name;
    private String value;
    private String imageUrl;

    public static ColorResponse from(Color color){
        ColorResponse result = new ColorResponse();
        result.id = color.getId();
        result.name = color.getName();
        result.value = color.getValue();
        result.imageUrl = color.getImageUrl();
        return result;
    }
}

