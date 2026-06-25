package com.fashionstore.services;

import com.fashionstore.dto.response.ColorResponse;
import com.fashionstore.exceptions.ConflictException;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.repositories.ColorRepository;
import com.fashionstore.repositories.ItemVariantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
    @Mock private ColorRepository colorRepositoryMock;
    @Mock private ItemVariantRepository itemVariantRepositoryMock;
    private ColorService objectUnderTest;

    @BeforeEach
    void setUp() {
        objectUnderTest = new ColorService(colorRepositoryMock, itemVariantRepositoryMock);
    }

    @Test
    void createColor_whenRequestIsValid_returnsCreatedColor() {
        var createdColor = color(1L, "Red", "#ff0000");

        when(colorRepositoryMock.save(any())).thenReturn(createdColor);

        ColorResponse response = objectUnderTest.createColor(colorRequest("Red", "#ff0000"));

        assertEquals("Red", response.getName(), "Created color name should match saved entity");
    }

    @Test
    void updateColor_whenColorExists_returnsUpdatedColor() {
        var createdColor = color(1L, "Red", "#ff0000");
        var updatedColor = color(1L, "Blue", "#0000ff");

        when(colorRepositoryMock.findById(1L)).thenReturn(Optional.of(createdColor));
        when(colorRepositoryMock.save(createdColor)).thenReturn(updatedColor);

        ColorResponse response = objectUnderTest.updateColor(1L, colorRequest("Blue", "#0000ff"));

        assertEquals("Blue", response.getName(), "Updated color name should match saved entity");
    }

    @Test
    void getPagedColors_whenColorsExist_returnsPagedColors() {
        var updatedColor = color(1L, "Blue", "#0000ff");

        when(colorRepositoryMock.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(updatedColor)));

        var response = objectUnderTest.getPagedColors(1, 20);

        assertFalse(response.getContent().isEmpty(), "Color page should contain repository results");
    }

    @Test
    void deleteColor_whenColorIsUnused_deletesColor() {
        when(colorRepositoryMock.existsById(1L)).thenReturn(true);
        when(itemVariantRepositoryMock.existsByColorId(1L)).thenReturn(false);

        objectUnderTest.deleteColor(1L);

        verify(colorRepositoryMock).deleteById(1L);
    }

    @Test
    void getColorById_whenColorIsMissing_throwsNotFoundException() {
        when(colorRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> objectUnderTest.getColorById(1L));
    }

    @Test
    void deleteColor_whenColorIsUsedByVariants_throwsConflictException() {
        when(colorRepositoryMock.existsById(1L)).thenReturn(true);
        when(itemVariantRepositoryMock.existsByColorId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> objectUnderTest.deleteColor(1L));
        verify(colorRepositoryMock, never()).deleteById(1L);
    }
}
