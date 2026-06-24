package com.fashionstore.services;

import com.fashionstore.dto.response.SizeResponse;
import com.fashionstore.exceptions.ConflictException;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.repositories.ItemVariantRepository;
import com.fashionstore.repositories.SizeRepository;
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
class SizeServiceTest extends ServiceTestSupport {
    @Mock private SizeRepository sizeRepository;
    @Mock private ItemVariantRepository itemVariantRepository;
    @InjectMocks private SizeService sizeService;

    @Test
    void createsUpdatesListsAndDeletesSizes() {
        var createdSize = size(1L, "M");
        var updatedSize = size(1L, "L");
        when(sizeRepository.save(any())).thenReturn(createdSize);
        SizeResponse created = sizeService.createSize(sizeRequest("M"));

        when(sizeRepository.findById(created.getId())).thenReturn(Optional.of(createdSize));
        when(sizeRepository.save(createdSize)).thenReturn(updatedSize);
        SizeResponse updated = sizeService.updateSize(created.getId(), sizeRequest("L"));

        when(sizeRepository.findAll()).thenReturn(List.of(updatedSize));
        assertEquals("L", updated.getLabel());
        assertFalse(sizeService.getAllSizes().isEmpty());

        when(itemVariantRepository.existsBySizeId(created.getId())).thenReturn(false);
        sizeService.deleteSize(created.getId());
        verify(sizeRepository).deleteById(created.getId());
    }

    @Test
    void throwsWhenSizeIsMissing() {
        when(sizeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sizeService.getSizeById(1L));
    }

    @Test
    void blocksDeletingSizeUsedByVariants() {
        when(itemVariantRepository.existsBySizeId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> sizeService.deleteSize(1L));
        verify(sizeRepository, never()).deleteById(1L);
    }
}
