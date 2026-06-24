package com.fashionstore.services;

import com.fashionstore.dto.response.FavoriteResponse;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.repositories.FavoriteRepository;
import com.fashionstore.repositories.ItemVariantRepository;
import com.fashionstore.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest extends ServiceTestSupport {
    @Mock private FavoriteRepository favoriteRepository;
    @Mock private UserRepository userRepository;
    @Mock private ItemVariantRepository itemVariantRepository;
    @InjectMocks private FavoriteService favoriteService;

    @Test
    void addsUpdatesQueriesAndDeletesFavorites() {
        var user = user(1L, "favorite-service@example.com");
        var category = category(1L, "Shirts");
        var item = item(1L, "Favorite Shirt", category);
        var size = size(1L, "M");
        var color = color(1L, "Blue", "#0000ff");
        var variant = itemVariant(1L, item, size, color);
        var favorite = favorite(1L, user, variant);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemVariantRepository.findById(variant.getId())).thenReturn(Optional.of(variant));
        when(favoriteRepository.save(any())).thenReturn(favorite);
        FavoriteResponse created = favoriteService.addFavorite(favoriteRequest(user.getId(), variant.getId()));

        when(favoriteRepository.findByUserId(user.getId())).thenReturn(List.of(favorite));
        when(favoriteRepository.findById(created.getId())).thenReturn(Optional.of(favorite));
        when(favoriteRepository.save(favorite)).thenReturn(favorite);
        assertEquals(user.getId(), created.getUserId());
        assertEquals(1, favoriteService.getFavoriteByUserId(user.getId()).size());
        assertEquals(created.getId(), favoriteService.updateFavorite(created.getId(), favoriteRequest(user.getId(), variant.getId())).getId());

        favoriteService.deleteFavorite(created.getId());
        verify(favoriteRepository).deleteById(created.getId());
    }

    @Test
    void throwsWhenFavoriteIsMissing() {
        when(favoriteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> favoriteService.getFavoriteById(1L));
    }
}
