package com.fashionstore.services;

import com.fashionstore.exceptions.ConflictException;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.models.Category;
import com.fashionstore.dto.request.CategoryRequest;
import com.fashionstore.dto.response.CategoryResponse;
import com.fashionstore.repositories.CategoryRepository;
import com.fashionstore.repositories.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;

    public CategoryService(CategoryRepository categoryRepository, ItemRepository itemRepository) {
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        Category category = new Category();
        category.setName(categoryRequest.getName());

        if (categoryRequest.getParentId() != null) {
            category.setParent(categoryRepository.findById(categoryRequest.getParentId())
                    .orElseThrow(() -> new NotFoundException("Category", categoryRequest.getParentId())));
        }

        Category savedCategory = categoryRepository.save(category);
        return CategoryResponse.from(savedCategory);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            category.setName(categoryRequest.getName());

            if (categoryRequest.getParentId() != null) {
                category.setParent(categoryRepository.findById(categoryRequest.getParentId())
                        .orElseThrow(() -> new NotFoundException("Category", categoryRequest.getParentId())));
            } else {
                category.setParent(null);
            }

            Category updatedCategory = categoryRepository.save(category);
            return CategoryResponse.from(updatedCategory);
        }
        throw new NotFoundException("Category", id);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        return categoryOptional.map(CategoryResponse::from).orElseThrow(() -> new NotFoundException("Category", id));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Category", id);
        }
        if (categoryRepository.existsByParentId(id)) {
            throw new ConflictException("Cannot delete category because it has subcategories. Delete or reassign them first.");
        }
        if (itemRepository.existsByCategoryId(id)) {
            throw new ConflictException("Cannot delete category because it has associated items. Reassign or delete them first.");
        }
        categoryRepository.deleteById(id);
    }
}

