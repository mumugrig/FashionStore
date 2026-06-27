package com.fashionstore.services;

import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.exceptions.ConflictException;
import com.fashionstore.models.Item;
import com.fashionstore.models.ItemVariant;
import com.fashionstore.models.Category;
import com.fashionstore.dto.request.ItemRequest;
import com.fashionstore.dto.response.AdminItemResponse;
import com.fashionstore.dto.response.ItemResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.repositories.ItemRepository;
import com.fashionstore.repositories.CategoryRepository;
import com.fashionstore.repositories.CartItemRepository;
import com.fashionstore.repositories.FavoriteRepository;
import com.fashionstore.repositories.ReviewRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final CartItemRepository cartItemRepository;
    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public ItemResponse createItem(ItemRequest itemRequest) {
        Item item = new Item();
        item.setName(itemRequest.getName());
        item.setPrice(itemRequest.getPrice());
        item.setDescription(itemRequest.getDescription());
        item.setImageUrl(itemRequest.getImageUrl());
        item.setAudience(itemRequest.getAudience());
        item.setCategory(findCategory(itemRequest.getCategoryId()));

        Item savedItem = itemRepository.save(item);
        return ItemResponse.from(savedItem);
    }

    @Transactional
    public ItemResponse updateItem(Long id, ItemRequest itemRequest) {
        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();
            item.setName(itemRequest.getName());
            item.setPrice(itemRequest.getPrice());
            item.setDescription(itemRequest.getDescription());
            item.setImageUrl(itemRequest.getImageUrl());
            item.setAudience(itemRequest.getAudience());
            item.setCategory(findCategory(itemRequest.getCategoryId()));

            Item updatedItem = itemRepository.save(item);
            return ItemResponse.from(updatedItem);
        }
        throw new NotFoundException("Item", id);
    }

    @Transactional(readOnly = true)
    public ItemResponse getItemById(Long id) {
        Optional<Item> itemOptional = itemRepository.findById(id);
        return itemOptional.map(ItemResponse::from).orElseThrow(() -> new NotFoundException("Item", id));
    }

    @Transactional(readOnly = true)
    public PageResponse<ItemResponse> getPagedItems(int page, int size) {
        return PageResponse.from(itemRepository.findAll(PageRequestFactory.create(page, size)), ItemResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<ItemResponse> getPagedItems(int page, int size, String search, String filterColumn, String filterValue) {
        if (!AdminFilterSpecification.hasFilters(search, filterColumn, filterValue)) {
            return getPagedItems(page, size);
        }
        return PageResponse.from(itemRepository.findAll(
                AdminFilterSpecification.create(adminFields(), search, filterColumn, filterValue),
                PageRequestFactory.create(page, size)
        ), ItemResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminItemResponse> getPagedAdminItems(int page, int size, String search, String filterColumn, String filterValue) {
        if (!AdminFilterSpecification.hasFilters(search, filterColumn, filterValue)) {
            return PageResponse.from(itemRepository.findAll(PageRequestFactory.create(page, size)), AdminItemResponse::from);
        }
        return PageResponse.from(itemRepository.findAll(
                AdminFilterSpecification.create(adminFields(), search, filterColumn, filterValue),
                PageRequestFactory.create(page, size)
        ), AdminItemResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<ItemResponse> getPagedItems(
            int page,
            int size,
            String category,
            String search,
            String itemSize,
            String color,
            String audience,
            BigDecimal priceMin,
            BigDecimal priceMax) {
        if (!hasText(category) && !hasText(search) && !hasText(itemSize) && !hasText(color)
                && !hasText(audience) && priceMin == null && priceMax == null) {
            return getPagedItems(page, size);
        }
        return PageResponse.from(itemRepository.findAll(
                itemFilters(category, search, itemSize, color, audience, priceMin, priceMax),
                PageRequestFactory.create(page, size)
        ), ItemResponse::from);
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> getItemsByCategory(Long categoryId) {
        return itemRepository.findByCategoryId(categoryId)
                .stream()
                .map(ItemResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new NotFoundException("Item", id);
        }
        if (cartItemRepository.existsByItemVariantItemId(id)) {
            throw new ConflictException("Cannot delete item because one or more variants are in carts. Remove those cart items first.");
        }
        if (favoriteRepository.existsByItemVariantItemId(id)) {
            throw new ConflictException("Cannot delete item because one or more variants are in favourites. Remove those favourites first.");
        }
        if (reviewRepository.existsByItemVariantItemId(id)) {
            throw new ConflictException("Cannot delete item because one or more variants have reviews. Delete those reviews first.");
        }
        itemRepository.deleteById(id);
    }

    @Transactional
    public void deleteItems(List<Long> ids) {
        ids.forEach(this::deleteItem);
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category", id));
    }

    private Specification<Item> itemFilters(
            String category,
            String search,
            String itemSize,
            String color,
            String audience,
            BigDecimal priceMin,
            BigDecimal priceMax) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            var predicate = criteriaBuilder.conjunction();

            if (hasText(category)) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(criteriaBuilder.lower(root.join("category").get("name")), normalized(category)));
            }
            if (hasText(search)) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + normalized(search) + "%"));
            }
            Join<Item, ItemVariant> variant = null;
            if (hasText(itemSize) || hasText(color)) {
                variant = root.join("variants", JoinType.INNER);
            }
            if (hasText(itemSize)) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(criteriaBuilder.lower(variant.join("size").get("label")), normalized(itemSize)));
            }
            if (hasText(color)) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(criteriaBuilder.lower(variant.join("color").get("name")), normalized(color)));
            }
            if (hasText(audience)) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(criteriaBuilder.lower(root.get("audience").as(String.class)), normalized(audience)));
            }
            if (priceMin != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("price"), priceMin));
            }
            if (priceMax != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("price"), priceMax));
            }

            return predicate;
        };
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String normalized(String value) {
        return value.trim().toLowerCase();
    }

    private Map<String, Function<Root<Item>, Expression<?>>> adminFields() {
        return Map.ofEntries(
                Map.entry("id", root -> root.get("id")),
                Map.entry("name", root -> root.get("name")),
                Map.entry("price", root -> root.get("price")),
                Map.entry("description", root -> root.get("description")),
                Map.entry("imageUrl", root -> root.get("imageUrl")),
                Map.entry("audience", root -> root.get("audience")),
                Map.entry("categoryId", root -> root.get("category").get("id")),
                Map.entry("categoryName", root -> root.get("category").get("name"))
        );
    }
}

