package com.fashionstore.services;

import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.exceptions.ConflictException;
import com.fashionstore.models.ItemVariant;
import com.fashionstore.models.Item;
import com.fashionstore.models.Size;
import com.fashionstore.models.Color;
import com.fashionstore.dto.request.ItemVariantRequest;
import com.fashionstore.dto.response.AdminItemVariantResponse;
import com.fashionstore.dto.response.ItemVariantResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.repositories.ItemVariantRepository;
import com.fashionstore.repositories.ItemRepository;
import com.fashionstore.repositories.SizeRepository;
import com.fashionstore.repositories.ColorRepository;
import com.fashionstore.repositories.CartItemRepository;
import com.fashionstore.repositories.FavoriteRepository;
import com.fashionstore.repositories.ReviewRepository;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemVariantService {
    private final ItemVariantRepository itemVariantRepository;
    private final ItemRepository itemRepository;
    private final SizeRepository sizeRepository;
    private final ColorRepository colorRepository;
    private final CartItemRepository cartItemRepository;
    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public ItemVariantResponse createItemVariant(ItemVariantRequest itemVariantRequest) {
        ItemVariant itemVariant = new ItemVariant();
        itemVariant.setActive(itemVariantRequest.isActive());
        itemVariant.setStockLeft(itemVariantRequest.getStockLeft());
        itemVariant.setImageUrl(itemVariantRequest.getImageUrl());

        Item item = itemRepository.findById(itemVariantRequest.getItemId())
                .orElseThrow(() -> new NotFoundException("Item", itemVariantRequest.getItemId()));
        itemVariant.setItem(item);

        Size size = sizeRepository.findById(itemVariantRequest.getSizeId())
                .orElseThrow(() -> new NotFoundException("Size", itemVariantRequest.getSizeId()));
        itemVariant.setSize(size);

        Color color = colorRepository.findById(itemVariantRequest.getColorId())
                .orElseThrow(() -> new NotFoundException("Color", itemVariantRequest.getColorId()));
        itemVariant.setColor(color);

        ItemVariant savedItemVariant = itemVariantRepository.save(itemVariant);
        return ItemVariantResponse.from(savedItemVariant);
    }

    @Transactional
    public ItemVariantResponse updateItemVariant(Long id, ItemVariantRequest itemVariantRequest) {
        Optional<ItemVariant> itemVariantOptional = itemVariantRepository.findById(id);
        if (itemVariantOptional.isPresent()) {
            ItemVariant itemVariant = itemVariantOptional.get();
            itemVariant.setActive(itemVariantRequest.isActive());
            itemVariant.setStockLeft(itemVariantRequest.getStockLeft());
            itemVariant.setImageUrl(itemVariantRequest.getImageUrl());

            Item item = itemRepository.findById(itemVariantRequest.getItemId())
                    .orElseThrow(() -> new NotFoundException("Item", itemVariantRequest.getItemId()));
            itemVariant.setItem(item);

            Size size = sizeRepository.findById(itemVariantRequest.getSizeId())
                    .orElseThrow(() -> new NotFoundException("Size", itemVariantRequest.getSizeId()));
            itemVariant.setSize(size);

            Color color = colorRepository.findById(itemVariantRequest.getColorId())
                    .orElseThrow(() -> new NotFoundException("Color", itemVariantRequest.getColorId()));
            itemVariant.setColor(color);

            ItemVariant updatedItemVariant = itemVariantRepository.save(itemVariant);
            return ItemVariantResponse.from(updatedItemVariant);
        }
        throw new NotFoundException("ItemVariant", id);
    }

    @Transactional(readOnly = true)
    public ItemVariantResponse getItemVariantById(Long id) {
        Optional<ItemVariant> itemVariantOptional = itemVariantRepository.findById(id);
        return itemVariantOptional.map(ItemVariantResponse::from).orElseThrow(() -> new NotFoundException("ItemVariant", id));
    }

    @Transactional(readOnly = true)
    public List<ItemVariantResponse> getAllItemVariants() {
        return itemVariantRepository.findAll()
                .stream()
                .map(ItemVariantResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminItemVariantResponse> getPagedAdminItemVariants(
            int page,
            int size,
            String search,
            String filterColumn,
            String filterValue) {
        if (!AdminFilterSpecification.hasFilters(search, filterColumn, filterValue)) {
            return PageResponse.from(itemVariantRepository.findAll(PageRequestFactory.create(page, size)), AdminItemVariantResponse::from);
        }
        return PageResponse.from(itemVariantRepository.findAll(
                AdminFilterSpecification.create(adminFields(), search, filterColumn, filterValue),
                PageRequestFactory.create(page, size)
        ), AdminItemVariantResponse::from);
    }

    @Transactional(readOnly = true)
    public List<ItemVariantResponse> getActiveVariantsByItem(Long itemId) {
        return itemVariantRepository.findByItemIdAndIsActiveTrue(itemId)
                .stream()
                .map(ItemVariantResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteItemVariant(Long id) {
        if (!itemVariantRepository.existsById(id)) {
            throw new NotFoundException("ItemVariant", id);
        }
        if (cartItemRepository.existsByItemVariantId(id)) {
            throw new ConflictException("Cannot delete item variant because it is in carts. Remove those cart items first.");
        }
        if (favoriteRepository.existsByItemVariantId(id)) {
            throw new ConflictException("Cannot delete item variant because it is in favourites. Remove those favourites first.");
        }
        if (reviewRepository.existsByItemVariantId(id)) {
            throw new ConflictException("Cannot delete item variant because it has reviews. Delete those reviews first.");
        }
        itemVariantRepository.deleteById(id);
    }

    @Transactional
    public void deleteItemVariants(List<Long> ids) {
        ids.forEach(this::deleteItemVariant);
    }

    private Map<String, Function<Root<ItemVariant>, Expression<?>>> adminFields() {
        return Map.ofEntries(
                Map.entry("id", root -> root.get("id")),
                Map.entry("active", root -> root.get("isActive")),
                Map.entry("isActive", root -> root.get("isActive")),
                Map.entry("stockLeft", root -> root.get("stockLeft")),
                Map.entry("imageUrl", root -> root.get("imageUrl")),
                Map.entry("itemId", root -> root.get("item").get("id")),
                Map.entry("itemName", root -> root.get("item").get("name")),
                Map.entry("itemPrice", root -> root.get("item").get("price")),
                Map.entry("itemAudience", root -> root.get("item").get("audience")),
                Map.entry("sizeId", root -> root.get("size").get("id")),
                Map.entry("sizeLabel", root -> root.get("size").get("label")),
                Map.entry("sizeSystem", root -> root.get("size").get("sizeSystem")),
                Map.entry("colorId", root -> root.get("color").get("id")),
                Map.entry("colorName", root -> root.get("color").get("name")),
                Map.entry("colorValue", root -> root.get("color").get("value"))
        );
    }
}

