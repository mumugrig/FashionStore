package com.fashionstore.services;

import com.fashionstore.models.Category;
import com.fashionstore.dto.request.CategoryRequest;
import com.fashionstore.dto.response.CategoryResponse;
import com.fashionstore.repositories.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        Category category = new Category();
        category.setName(categoryRequest.getName());

        if (categoryRequest.getParentId() != null) {
            Optional<Category> parentCategory = categoryRepository.findById(categoryRequest.getParentId());
            parentCategory.ifPresent(category::setParent);
        }

        Category savedCategory = categoryRepository.save(category);
        return CategoryResponse.from(savedCategory);
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            category.setName(categoryRequest.getName());

            if (categoryRequest.getParentId() != null) {
                Optional<Category> parentCategory = categoryRepository.findById(categoryRequest.getParentId());
                parentCategory.ifPresent(category::setParent);
            } else {
                category.setParent(null);
            }

            Category updatedCategory = categoryRepository.save(category);
            return CategoryResponse.from(updatedCategory);
        }
        return null;
    }

    public CategoryResponse getCategoryById(Long id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        return categoryOptional.map(CategoryResponse::from).orElse(null);
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}

