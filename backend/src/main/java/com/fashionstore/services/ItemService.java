package com.fashionstore.services;

import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.models.Item;
import com.fashionstore.models.Category;
import com.fashionstore.vo.Audience;
import com.fashionstore.dto.request.ItemRequest;
import com.fashionstore.dto.response.ItemResponse;
import com.fashionstore.repositories.ItemRepository;
import com.fashionstore.repositories.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    public ItemService(ItemRepository itemRepository, CategoryRepository categoryRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
    }

    public ItemResponse createItem(ItemRequest itemRequest) {
        Item item = new Item();
        item.setName(itemRequest.getName());
        item.setPrice(itemRequest.getPrice());
        item.setDescription(itemRequest.getDescription());
        item.setAudience(Audience.valueOf(itemRequest.getAudience()));

        Optional<Category> category = categoryRepository.findById(itemRequest.getCategoryId());
        category.ifPresent(item::setCategory);

        Item savedItem = itemRepository.save(item);
        return ItemResponse.from(savedItem);
    }

    public ItemResponse updateItem(Long id, ItemRequest itemRequest) {
        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();
            item.setName(itemRequest.getName());
            item.setPrice(itemRequest.getPrice());
            item.setDescription(itemRequest.getDescription());
            item.setAudience(Audience.valueOf(itemRequest.getAudience()));

            Optional<Category> category = categoryRepository.findById(itemRequest.getCategoryId());
            category.ifPresent(item::setCategory);

            Item updatedItem = itemRepository.save(item);
            return ItemResponse.from(updatedItem);
        }
        throw new NotFoundException("Item", id);
    }

    public ItemResponse getItemById(Long id) {
        Optional<Item> itemOptional = itemRepository.findById(id);
        return itemOptional.map(ItemResponse::from).orElseThrow(() -> new NotFoundException("Item", id));
    }

    public List<ItemResponse> getAllItems() {
        return itemRepository.findAll()
                .stream()
                .map(ItemResponse::from)
                .collect(Collectors.toList());
    }

    public List<ItemResponse> getItemsByCategory(Long categoryId) {
        return itemRepository.findByCategoryId(categoryId)
                .stream()
                .map(ItemResponse::from)
                .collect(Collectors.toList());
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
}

