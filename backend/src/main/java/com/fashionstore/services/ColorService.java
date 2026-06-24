package com.fashionstore.services;

import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.models.Color;
import com.fashionstore.dto.request.ColorRequest;
import com.fashionstore.dto.response.ColorResponse;
import com.fashionstore.exceptions.ConflictException;
import com.fashionstore.repositories.ColorRepository;
import com.fashionstore.repositories.ItemVariantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ColorService {
    private final ColorRepository colorRepository;
    private final ItemVariantRepository itemVariantRepository;

    public ColorService(ColorRepository colorRepository, ItemVariantRepository itemVariantRepository) {
        this.colorRepository = colorRepository;
        this.itemVariantRepository = itemVariantRepository;
    }

    @Transactional
    public ColorResponse createColor(ColorRequest colorRequest) {
        Color color = new Color();
        color.setName(colorRequest.getName());
        color.setValue(colorRequest.getValue());
        color.setImageUrl(colorRequest.getImageUrl());

        Color savedColor = colorRepository.save(color);
        return ColorResponse.from(savedColor);
    }

    @Transactional
    public ColorResponse updateColor(Long id, ColorRequest colorRequest) {
        Optional<Color> colorOptional = colorRepository.findById(id);
        if (colorOptional.isPresent()) {
            Color color = colorOptional.get();
            color.setName(colorRequest.getName());
            color.setValue(colorRequest.getValue());
            color.setImageUrl(colorRequest.getImageUrl());

            Color updatedColor = colorRepository.save(color);
            return ColorResponse.from(updatedColor);
        }
        throw new NotFoundException("Color", id);
    }

    @Transactional(readOnly = true)
    public ColorResponse getColorById(Long id) {
        Optional<Color> colorOptional = colorRepository.findById(id);
        return colorOptional.map(ColorResponse::from).orElseThrow(() -> new NotFoundException("Color", id));
    }

    @Transactional(readOnly = true)
    public List<ColorResponse> getAllColors() {
        return colorRepository.findAll()
                .stream()
                .map(ColorResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteColor(Long id) {
        if (!colorRepository.existsById(id)) {
            throw new NotFoundException("Color", id);
        }
        if (itemVariantRepository.existsByColorId(id)) {
            throw new ConflictException("Cannot delete color because item variants use it. Reassign or delete them first.");
        }
        colorRepository.deleteById(id);
    }
}

