package com.fashionstore.controllers;

import com.fashionstore.services.SizeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SizeControllerTest extends ControllerTestSupport {
    @Mock private SizeService sizeService;
    @InjectMocks private SizeController sizeController;

    @Test
    void createsUpdatesListsAndDeletesSizes() {
        when(sizeService.createSize(any())).thenReturn(sizeResponse(1L, "M"));
        when(sizeService.updateSize(org.mockito.ArgumentMatchers.eq(1L), any())).thenReturn(sizeResponse(1L, "L"));
        when(sizeService.getAllSizes()).thenReturn(List.of(sizeResponse(1L, "L")));

        var created = sizeController.createSize(sizeRequest("M"));
        var updated = sizeController.updateSize(1L, sizeRequest("L"));
        var listed = sizeController.getAllSizes();
        var deleted = sizeController.deleteSize(1L);

        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        assertEquals("L", updated.getBody().getLabel());
        assertEquals(1, listed.getBody().size());
        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());
        verify(sizeService).deleteSize(1L);
    }
}
