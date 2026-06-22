package com.fashionstore.dto.response;

import com.fashionstore.models.Category;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private Long parentId;

    public static CategoryResponse from(Category category) {
        CategoryResponse result = new CategoryResponse();
        result.id = category.getId();
        result.name = category.getName();
        result.parentId = category.getParent() != null ? category.getParent().getId() : null;
        return result;
    }
}

