package com.fashionstore.services;

import com.fashionstore.exceptions.ConflictException;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.models.Category;
import com.fashionstore.dto.request.CategoryRequest;
import com.fashionstore.dto.response.CategoryResponse;
import com.fashionstore.repositories.CategoryRepository;
import com.fashionstore.repositories.ItemRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;

    public CategoryService(CategoryRepository categoryRepository, ItemRepository itemRepository) {
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
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
        throw new NotFoundException("Category", id);
    }

    public CategoryResponse getCategoryById(Long id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        return categoryOptional.map(CategoryResponse::from).orElseThrow(() -> new NotFoundException("Category", id));
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

    public void deleteCategory(Long id) {
        if (itemRepository.existsByCategoryId(id)) {
            throw new ConflictException("Cannot delete category because it has associated items. Reassign or delete them first.");
        }
        categoryRepository.deleteById(id);
    }
}

