package com.fashionstore.controllers;

import com.fashionstore.services.ColorService;
import com.fashionstore.controllers.admin.AdminColorController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ColorControllerTest extends ControllerTestSupport {
    @Mock private ColorService colorServiceMock;
    private AdminColorController objectUnderTest;

    @BeforeEach
    void setUp() {
        objectUnderTest = new AdminColorController(colorServiceMock);
    }

    @Test
    void createColor_whenRequestIsValid_returnsCreatedColor() {
        when(colorServiceMock.createColor(any())).thenReturn(colorResponse(1L, "Black", "#000000"));

        var response = objectUnderTest.createColor(colorRequest("Black", "#000000"));

        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Color creation should return HTTP 201");
        assertEquals("Black", response.getBody().getName(), "Created color name should match service response");
    }

    @Test
    void updateColor_whenColorExists_returnsUpdatedColor() {
        when(colorServiceMock.updateColor(org.mockito.ArgumentMatchers.eq(1L), any()))
                .thenReturn(colorResponse(1L, "White", "#ffffff"));

        var response = objectUnderTest.updateColor(1L, colorRequest("White", "#ffffff"));

        assertEquals("White", response.getBody().getName(), "Updated color name should match service response");
    }

    @Test
    void getColors_whenPageIsRequested_returnsPagedColors() {
        when(colorServiceMock.getPagedColors(1, 20, null, null, null)).thenReturn(pageResponse(colorResponse(1L, "White", "#ffffff")));

        var response = objectUnderTest.getPagedColors(1, 20, null, null, null);

        assertEquals(1, response.getBody().getContent().size(), "Color page should contain service results");
    }

    @Test
    void deleteColor_whenColorExists_returnsNoContent() {
        var response = objectUnderTest.deleteColor(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "Color deletion should return HTTP 204");
        verify(colorServiceMock).deleteColor(1L);
    }
}
