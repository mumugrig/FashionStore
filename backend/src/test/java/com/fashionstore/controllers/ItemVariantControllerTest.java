package com.fashionstore.controllers;

import com.fashionstore.controllers.admin.AdminItemVariantController;
import com.fashionstore.dto.request.BulkDeleteRequest;
import com.fashionstore.services.ItemVariantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemVariantControllerTest extends ControllerTestSupport {
    @Mock private ItemVariantService itemVariantServiceMock;
    private AdminItemVariantController objectUnderTest;

    @BeforeEach
    void setUp() {
        objectUnderTest = new AdminItemVariantController(itemVariantServiceMock);
    }

    @Test
    void getPagedItemVariants_returnsEnrichedVariantFields() {
        when(itemVariantServiceMock.getPagedAdminItemVariants(1, 20, null, null, null))
                .thenReturn(pageResponse(adminItemVariantResponse(1L, 2L, 3L, 4L)));

        var response = objectUnderTest.getPagedItemVariants(1, 20, null, null, null);

        assertEquals(1, response.getBody().getContent().size());
        assertEquals("Rain Jacket", response.getBody().getContent().get(0).getItemName());
        assertEquals("Black", response.getBody().getContent().get(0).getColorName());
    }

    @Test
    void createItemVariant_whenRequestIsValid_returnsCreatedVariant() {
        when(itemVariantServiceMock.createItemVariant(any())).thenReturn(itemVariantResponse(1L, 2L, 3L, 4L));

        var response = objectUnderTest.createItemVariant(itemVariantRequest(2L, 3L, 4L));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void updateItemVariant_whenRequestIsValid_returnsUpdatedVariant() {
        when(itemVariantServiceMock.updateItemVariant(eq(1L), any())).thenReturn(itemVariantResponse(1L, 2L, 3L, 4L));

        var response = objectUnderTest.updateItemVariant(1L, itemVariantRequest(2L, 3L, 4L));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(12, response.getBody().getStockLeft());
    }

    @Test
    void deleteItemVariant_whenVariantExists_returnsNoContent() {
        var response = objectUnderTest.deleteItemVariant(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(itemVariantServiceMock).deleteItemVariant(1L);
    }

    @Test
    void deleteItemVariants_callsServiceBulkDelete() {
        var response = objectUnderTest.deleteItemVariants(new BulkDeleteRequest(List.of(1L, 2L)));

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(itemVariantServiceMock).deleteItemVariants(List.of(1L, 2L));
    }
}
