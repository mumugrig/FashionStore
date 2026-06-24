package com.fashionstore.services;

import com.fashionstore.dto.response.ColorResponse;
import com.fashionstore.exceptions.ConflictException;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.repositories.ColorRepository;
import com.fashionstore.repositories.ItemVariantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ColorServiceTest extends ServiceTestSupport {
    @Mock private ColorRepository colorRepository;
    @Mock private ItemVariantRepository itemVariantRepository;
    @InjectMocks private ColorService colorService;

    @Test
    void createsUpdatesListsAndDeletesColors() {
        var createdColor = color(1L, "Red", "#ff0000");
        var updatedColor = color(1L, "Blue", "#0000ff");
        when(colorRepository.save(any())).thenReturn(createdColor);
        ColorResponse created = colorService.createColor(colorRequest("Red", "#ff0000"));

        when(colorRepository.findById(created.getId())).thenReturn(Optional.of(createdColor));
        when(colorRepository.save(createdColor)).thenReturn(updatedColor);
        ColorResponse updated = colorService.updateColor(created.getId(), colorRequest("Blue", "#0000ff"));

        when(colorRepository.findAll()).thenReturn(List.of(updatedColor));
        assertEquals("Blue", updated.getName());
        assertFalse(colorService.getAllColors().isEmpty());

        when(itemVariantRepository.existsByColorId(created.getId())).thenReturn(false);
        colorService.deleteColor(created.getId());
        verify(colorRepository).deleteById(created.getId());
    }

    @Test
    void throwsWhenColorIsMissing() {
        when(colorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> colorService.getColorById(1L));
    }

    @Test
    void blocksDeletingColorUsedByVariants() {
        when(itemVariantRepository.existsByColorId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> colorService.deleteColor(1L));
        verify(colorRepository, never()).deleteById(1L);
    }
}
