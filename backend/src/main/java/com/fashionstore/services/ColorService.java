package com.fashionstore.services;

import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.models.Color;
import com.fashionstore.dto.request.ColorRequest;
import com.fashionstore.dto.response.ColorResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.exceptions.ConflictException;
import com.fashionstore.repositories.ColorRepository;
import com.fashionstore.repositories.ItemVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ColorService {
    private final ColorRepository colorRepository;
    private final ItemVariantRepository itemVariantRepository;

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
    public PageResponse<ColorResponse> getPagedColors(int page, int size) {
        return PageResponse.from(colorRepository.findAll(PageRequestFactory.create(page, size)), ColorResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<ColorResponse> getPagedColors(int page, int size, String search, String filterColumn, String filterValue) {
        if (!AdminFilterSpecification.hasFilters(search, filterColumn, filterValue)) {
            return getPagedColors(page, size);
        }
        return PageResponse.from(colorRepository.findAll(
                AdminFilterSpecification.create(AdminSearchFields.COLORS, search, filterColumn, filterValue),
                PageRequestFactory.create(page, size)
        ), ColorResponse::from);
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

    @Transactional
    public void deleteColors(List<Long> ids) {
        ids.forEach(this::deleteColor);
    }
}

