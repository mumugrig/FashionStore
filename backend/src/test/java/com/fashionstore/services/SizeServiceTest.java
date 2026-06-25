package com.fashionstore.services;

import com.fashionstore.dto.response.SizeResponse;
import com.fashionstore.exceptions.ConflictException;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.repositories.ItemVariantRepository;
import com.fashionstore.repositories.SizeRepository;
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
class SizeServiceTest extends ServiceTestSupport {
    @Mock private SizeRepository sizeRepositoryMock;
    @Mock private ItemVariantRepository itemVariantRepositoryMock;
    private SizeService objectUnderTest;

    @BeforeEach
    void setUp() {
        objectUnderTest = new SizeService(sizeRepositoryMock, itemVariantRepositoryMock);
    }

    @Test
    void createSize_whenRequestIsValid_returnsCreatedSize() {
        var createdSize = size(1L, "M");

        when(sizeRepositoryMock.save(any())).thenReturn(createdSize);

        SizeResponse response = objectUnderTest.createSize(sizeRequest("M"));

        assertEquals("M", response.getLabel(), "Created size label should match saved entity");
    }

    @Test
    void updateSize_whenSizeExists_returnsUpdatedSize() {
        var createdSize = size(1L, "M");
        var updatedSize = size(1L, "L");

        when(sizeRepositoryMock.findById(1L)).thenReturn(Optional.of(createdSize));
        when(sizeRepositoryMock.save(createdSize)).thenReturn(updatedSize);

        SizeResponse response = objectUnderTest.updateSize(1L, sizeRequest("L"));

        assertEquals("L", response.getLabel(), "Updated size label should match saved entity");
    }

    @Test
    void getPagedSizes_whenSizesExist_returnsPagedSizes() {
        var updatedSize = size(1L, "L");

        when(sizeRepositoryMock.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(updatedSize)));

        var response = objectUnderTest.getPagedSizes(1, 20);

        assertFalse(response.getContent().isEmpty(), "Size page should contain repository results");
    }

    @Test
    void deleteSize_whenSizeIsUnused_deletesSize() {
        when(sizeRepositoryMock.existsById(1L)).thenReturn(true);
        when(itemVariantRepositoryMock.existsBySizeId(1L)).thenReturn(false);

        objectUnderTest.deleteSize(1L);

        verify(sizeRepositoryMock).deleteById(1L);
    }

    @Test
    void getSizeById_whenSizeIsMissing_throwsNotFoundException() {
        when(sizeRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> objectUnderTest.getSizeById(1L));
    }

    @Test
    void deleteSize_whenSizeIsUsedByVariants_throwsConflictException() {
        when(sizeRepositoryMock.existsById(1L)).thenReturn(true);
        when(itemVariantRepositoryMock.existsBySizeId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> objectUnderTest.deleteSize(1L));
        verify(sizeRepositoryMock, never()).deleteById(1L);
    }
}
