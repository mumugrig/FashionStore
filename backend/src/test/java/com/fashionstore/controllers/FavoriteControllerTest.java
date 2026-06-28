package com.fashionstore.controllers;

import com.fashionstore.services.FavoriteService;
import com.fashionstore.controllers.admin.AdminFavoriteController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteControllerTest extends ControllerTestSupport {
    @Mock private FavoriteService favoriteServiceMock;
    private AdminFavoriteController objectUnderTest;

    @BeforeEach
    void setUp() {
        objectUnderTest = new AdminFavoriteController(favoriteServiceMock);
    }

    @Test
    void addFavorite_whenRequestIsValid_returnsCreatedFavorite() {
        when(favoriteServiceMock.addFavorite(any())).thenReturn(favoriteResponse(1L, 1L, 2L));

        var response = objectUnderTest.addFavorite(favoriteRequest(1L, 2L));

        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Favorite creation should return HTTP 201");
        assertEquals(1L, response.getBody().getUserId(), "Created favorite user id should match service response");
    }

    @Test
    void getPagedFavoritesByUserId_whenPageIsRequested_returnsPagedFavorites() {
        when(favoriteServiceMock.getPagedFavoritesByUserId(1L, 1, 20)).thenReturn(pageResponse(favoriteResponse(1L, 1L, 2L)));

        var response = objectUnderTest.getPagedFavoritesByUserId(1L, 1, 20);

        assertEquals(1, response.getBody().getContent().size(), "Favorites page should contain service results");
    }

    @Test
    void getFavoriteById_whenFavoriteExists_returnsAdminFavorite() {
        when(favoriteServiceMock.getAdminFavoriteById(1L)).thenReturn(adminFavoriteResponse(1L, 1L, 2L));

        var response = objectUnderTest.getFavoriteById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Favorite lookup should return HTTP 200");
        assertEquals("Jacket", response.getBody().getItemName(), "Admin favorite lookup should include item details");
    }

    @Test
    void updateFavorite_whenRequestIsValid_returnsUpdatedFavorite() {
        when(favoriteServiceMock.updateFavorite(eq(1L), any())).thenReturn(favoriteResponse(1L, 1L, 2L));

        var response = objectUnderTest.updateFavorite(1L, favoriteRequest(1L, 2L));

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Favorite update should return HTTP 200");
        assertEquals(2L, response.getBody().getItemVariantId(), "Updated favorite variant should match service response");
    }

    @Test
    void removeFavorite_whenFavoriteExists_returnsNoContent() {
        var response = objectUnderTest.removeFavorite(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "Favorite deletion should return HTTP 204");
        verify(favoriteServiceMock).deleteFavorite(1L);
    }
}
