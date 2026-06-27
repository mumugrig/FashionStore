package com.fashionstore.dto.response;

import com.fashionstore.models.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminCategoryResponse extends CategoryResponse {
    private String parentName;

    public AdminCategoryResponse(CategoryResponse base) {
        setId(base.getId());
        setName(base.getName());
        setParentId(base.getParentId());
    }

    public static AdminCategoryResponse from(Category category) {
        AdminCategoryResponse result = new AdminCategoryResponse(CategoryResponse.from(category));
        result.parentName = category.getParent() != null ? category.getParent().getName() : null;
        return result;
    }
}
