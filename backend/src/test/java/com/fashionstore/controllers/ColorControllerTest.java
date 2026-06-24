package com.fashionstore.controllers;

import com.fashionstore.services.ColorService;
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
class ColorControllerTest extends ControllerTestSupport {
    @Mock private ColorService colorService;
    @InjectMocks private ColorController colorController;

    @Test
    void createsUpdatesListsAndDeletesColors() {
        when(colorService.createColor(any())).thenReturn(colorResponse(1L, "Black", "#000000"));
        when(colorService.updateColor(org.mockito.ArgumentMatchers.eq(1L), any())).thenReturn(colorResponse(1L, "White", "#ffffff"));
        when(colorService.getAllColors()).thenReturn(List.of(colorResponse(1L, "White", "#ffffff")));

        var created = colorController.createColor(colorRequest("Black", "#000000"));
        var updated = colorController.updateColor(1L, colorRequest("White", "#ffffff"));
        var listed = colorController.getColors();
        var deleted = colorController.deleteColor(1L);

        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        assertEquals("White", updated.getBody().getName());
        assertEquals(1, listed.getBody().size());
        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());
        verify(colorService).deleteColor(1L);
    }
}
