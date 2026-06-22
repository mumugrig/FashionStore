package com.fashionstore.services;

import com.fashionstore.models.Color;
import com.fashionstore.dto.request.ColorRequest;
import com.fashionstore.dto.response.ColorResponse;
import com.fashionstore.repositories.ColorRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ColorService {
    private final ColorRepository colorRepository;

    public ColorService(ColorRepository colorRepository) {
        this.colorRepository = colorRepository;
    }

    public ColorResponse createColor(ColorRequest colorRequest) {
        Color color = new Color();
        color.setName(colorRequest.getName());
        color.setValue(colorRequest.getValue());
        color.setImageUrl(colorRequest.getImageUrl());

        Color savedColor = colorRepository.save(color);
        return ColorResponse.from(savedColor);
    }

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
        return null;
    }

    public ColorResponse getColorById(Long id) {
        Optional<Color> colorOptional = colorRepository.findById(id);
        return colorOptional.map(ColorResponse::from).orElse(null);
    }

    public List<ColorResponse> getAllColors() {
        return colorRepository.findAll()
                .stream()
                .map(ColorResponse::from)
                .collect(Collectors.toList());
    }

    public void deleteColor(Long id) {
        colorRepository.deleteById(id);
    }
}

