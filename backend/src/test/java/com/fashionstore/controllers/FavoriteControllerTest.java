package com.fashionstore.controllers;

import com.fashionstore.services.FavoriteService;
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
class FavoriteControllerTest extends ControllerTestSupport {
    @Mock private FavoriteService favoriteService;
    @InjectMocks private FavoriteController favoriteController;

    @Test
    void createsListsAndDeletesFavorites() {
        when(favoriteService.addFavorite(any())).thenReturn(favoriteResponse(1L, 1L, 2L));
        when(favoriteService.getFavoriteByUserId(1L)).thenReturn(List.of(favoriteResponse(1L, 1L, 2L)));

        var created = favoriteController.addFavorite(favoriteRequest(1L, 2L));
        var listed = favoriteController.getFavoritesByUserId(1L);
        var deleted = favoriteController.removeFavorite(1L);

        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        assertEquals(1L, created.getBody().getUserId());
        assertEquals(1, listed.getBody().size());
        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());
        verify(favoriteService).deleteFavorite(1L);
    }
}
