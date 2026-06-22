package com.fashionstore.services;

import com.fashionstore.models.ItemVariant;
import com.fashionstore.models.Item;
import com.fashionstore.models.Size;
import com.fashionstore.models.Color;
import com.fashionstore.dto.request.ItemVariantRequest;
import com.fashionstore.dto.response.ItemVariantResponse;
import com.fashionstore.repositories.ItemVariantRepository;
import com.fashionstore.repositories.ItemRepository;
import com.fashionstore.repositories.SizeRepository;
import com.fashionstore.repositories.ColorRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemVariantService {
    private final ItemVariantRepository itemVariantRepository;
    private final ItemRepository itemRepository;
    private final SizeRepository sizeRepository;
    private final ColorRepository colorRepository;

    public ItemVariantService(ItemVariantRepository itemVariantRepository,
                            ItemRepository itemRepository,
                            SizeRepository sizeRepository,
                            ColorRepository colorRepository) {
        this.itemVariantRepository = itemVariantRepository;
        this.itemRepository = itemRepository;
        this.sizeRepository = sizeRepository;
        this.colorRepository = colorRepository;
    }

    public ItemVariantResponse createItemVariant(ItemVariantRequest itemVariantRequest) {
        ItemVariant itemVariant = new ItemVariant();
        itemVariant.setActive(itemVariantRequest.isActive());
        itemVariant.setStockLeft(itemVariantRequest.getStockLeft());

        Optional<Item> item = itemRepository.findById(itemVariantRequest.getItemId());
        item.ifPresent(itemVariant::setItem);

        Optional<Size> size = sizeRepository.findById(itemVariantRequest.getSizeId());
        size.ifPresent(itemVariant::setSize);

        Optional<Color> color = colorRepository.findById(itemVariantRequest.getColorId());
        color.ifPresent(itemVariant::setColor);

        ItemVariant savedItemVariant = itemVariantRepository.save(itemVariant);
        return ItemVariantResponse.from(savedItemVariant);
    }

    public ItemVariantResponse updateItemVariant(Long id, ItemVariantRequest itemVariantRequest) {
        Optional<ItemVariant> itemVariantOptional = itemVariantRepository.findById(id);
        if (itemVariantOptional.isPresent()) {
            ItemVariant itemVariant = itemVariantOptional.get();
            itemVariant.setActive(itemVariantRequest.isActive());
            itemVariant.setStockLeft(itemVariantRequest.getStockLeft());

            Optional<Item> item = itemRepository.findById(itemVariantRequest.getItemId());
            item.ifPresent(itemVariant::setItem);

            Optional<Size> size = sizeRepository.findById(itemVariantRequest.getSizeId());
            size.ifPresent(itemVariant::setSize);

            Optional<Color> color = colorRepository.findById(itemVariantRequest.getColorId());
            color.ifPresent(itemVariant::setColor);

            ItemVariant updatedItemVariant = itemVariantRepository.save(itemVariant);
            return ItemVariantResponse.from(updatedItemVariant);
        }
        return null;
    }

    public ItemVariantResponse getItemVariantById(Long id) {
        Optional<ItemVariant> itemVariantOptional = itemVariantRepository.findById(id);
        return itemVariantOptional.map(ItemVariantResponse::from).orElse(null);
    }

    public List<ItemVariantResponse> getAllItemVariants() {
        return itemVariantRepository.findAll()
                .stream()
                .map(ItemVariantResponse::from)
                .collect(Collectors.toList());
    }

    public List<ItemVariantResponse> getActiveVariantsByItem(Long itemId) {
        return itemVariantRepository.findByItemIdAndIsActiveTrue(itemId)
                .stream()
                .map(ItemVariantResponse::from)
                .collect(Collectors.toList());
    }

    public void deleteItemVariant(Long id) {
        itemVariantRepository.deleteById(id);
    }
}

