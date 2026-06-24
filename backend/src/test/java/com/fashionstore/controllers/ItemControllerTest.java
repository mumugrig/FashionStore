package com.fashionstore.controllers;

import com.fashionstore.services.ItemService;
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
class ItemControllerTest extends ControllerTestSupport {
    @Mock private ItemService itemService;
    @InjectMocks private ItemController itemController;

    @Test
    void createsUpdatesReadsListsAndDeletesItems() {
        when(itemService.createItem(any())).thenReturn(itemResponse(1L, "Jacket", 1L));
        when(itemService.updateItem(org.mockito.ArgumentMatchers.eq(1L), any())).thenReturn(itemResponse(1L, "Rain Jacket", 1L));
        when(itemService.getItemById(1L)).thenReturn(itemResponse(1L, "Rain Jacket", 1L));
        when(itemService.getAllItems()).thenReturn(List.of(itemResponse(1L, "Rain Jacket", 1L)));

        var created = itemController.createItem(itemRequest("Jacket", 1L));
        var updated = itemController.updateItem(1L, itemRequest("Rain Jacket", 1L));
        var fetched = itemController.getItemById(1L);
        var listed = itemController.getAllItems();
        var deleted = itemController.deleteItem(1L);

        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        assertEquals("Rain Jacket", updated.getBody().getName());
        assertEquals(1L, fetched.getBody().getId());
        assertEquals(1, listed.getBody().size());
        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());
        verify(itemService).deleteItem(1L);
    }
}
