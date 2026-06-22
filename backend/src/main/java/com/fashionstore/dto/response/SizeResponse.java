package com.fashionstore.dto.response;

import com.fashionstore.models.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SizeResponse {
    private Long id;
    private String label;
    private String sizeSystem;

    public static SizeResponse from(Size size){
        SizeResponse result = new SizeResponse();
        result.id = size.getId();
        result.label = size.getLabel();
        result.sizeSystem = size.getSizeSystem().name();
        return result;
    }
}

