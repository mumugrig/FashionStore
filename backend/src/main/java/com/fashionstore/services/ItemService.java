package com.fashionstore.services;

import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.exceptions.ConflictException;
import com.fashionstore.models.Item;
import com.fashionstore.models.Category;
import com.fashionstore.dto.request.ItemRequest;
import com.fashionstore.dto.response.ItemResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.repositories.ItemRepository;
import com.fashionstore.repositories.CategoryRepository;
import com.fashionstore.repositories.CartItemRepository;
import com.fashionstore.repositories.FavoriteRepository;
import com.fashionstore.repositories.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
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

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category", id));
    }
}

