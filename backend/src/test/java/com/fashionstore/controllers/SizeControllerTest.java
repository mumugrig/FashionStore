package com.fashionstore.controllers;

import com.fashionstore.services.SizeService;
import com.fashionstore.controllers.admin.AdminSizeController;
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
class SizeControllerTest extends ControllerTestSupport {
    @Mock private SizeService sizeServiceMock;
    private AdminSizeController objectUnderTest;

    @BeforeEach
    void setUp() {
        objectUnderTest = new AdminSizeController(sizeServiceMock);
    }

    @Test
    void createSize_whenRequestIsValid_returnsCreatedSize() {
        when(sizeServiceMock.createSize(any())).thenReturn(sizeResponse(1L, "M"));

        var response = objectUnderTest.createSize(sizeRequest("M"));

        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Size creation should return HTTP 201");
        assertEquals("M", response.getBody().getLabel(), "Created size label should match service response");
    }

    @Test
    void updateSize_whenSizeExists_returnsUpdatedSize() {
        when(sizeServiceMock.updateSize(org.mockito.ArgumentMatchers.eq(1L), any())).thenReturn(sizeResponse(1L, "L"));

        var response = objectUnderTest.updateSize(1L, sizeRequest("L"));

        assertEquals("L", response.getBody().getLabel(), "Updated size label should match service response");
    }

    @Test
    void getPagedSizes_whenPageIsRequested_returnsPagedSizes() {
        when(sizeServiceMock.getPagedSizes(1, 20, null, null, null)).thenReturn(pageResponse(sizeResponse(1L, "L")));

        var response = objectUnderTest.getPagedSizes(1, 20, null, null, null);

        assertEquals(1, response.getBody().getContent().size(), "Size page should contain service results");
    }

    @Test
    void deleteSize_whenSizeExists_returnsNoContent() {
        var response = objectUnderTest.deleteSize(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "Size deletion should return HTTP 204");
        verify(sizeServiceMock).deleteSize(1L);
    }
}
