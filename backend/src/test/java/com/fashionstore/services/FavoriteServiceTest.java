package com.fashionstore.services;

import com.fashionstore.dto.response.FavoriteResponse;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.repositories.FavoriteRepository;
import com.fashionstore.repositories.ItemVariantRepository;
import com.fashionstore.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest extends ServiceTestSupport {
    @Mock private FavoriteRepository favoriteRepositoryMock;
    @Mock private UserRepository userRepositoryMock;
    @Mock private ItemVariantRepository itemVariantRepositoryMock;
    @Mock private CurrentUserService currentUserServiceMock;
    private FavoriteService objectUnderTest;

    @BeforeEach
    void setUp() {
        objectUnderTest = new FavoriteService(favoriteRepositoryMock, userRepositoryMock, itemVariantRepositoryMock, currentUserServiceMock);
    }

    @Test
    void addFavorite_whenUserAndVariantExist_returnsCreatedFavorite() {
        var user = user(1L, "favorite-service@example.com");
        var category = category(1L, "Shirts");
        var item = item(1L, "Favorite Shirt", category);
        var size = size(1L, "M");
        var color = color(1L, "Blue", "#0000ff");
        var variant = itemVariant(1L, item, size, color);
        var favorite = favorite(1L, user, variant);

        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemVariantRepositoryMock.findById(variant.getId())).thenReturn(Optional.of(variant));
        when(favoriteRepositoryMock.save(any())).thenReturn(favorite);

        FavoriteResponse response = objectUnderTest.addFavorite(favoriteRequest(user.getId(), variant.getId()));

        assertEquals(user.getId(), response.getUserId(), "Created favorite should belong to the requested user");
    }

    @Test
    void getPagedFavoritesByUserId_whenFavoritesExist_returnsPagedFavorites() {
        var user = user(1L, "favorite-service@example.com");
        var category = category(1L, "Shirts");
        var item = item(1L, "Favorite Shirt", category);
        var size = size(1L, "M");
        var color = color(1L, "Blue", "#0000ff");
        var variant = itemVariant(1L, item, size, color);
        var favorite = favorite(1L, user, variant);

        when(userRepositoryMock.existsById(user.getId())).thenReturn(true);
        when(favoriteRepositoryMock.findByUserId(org.mockito.ArgumentMatchers.eq(user.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(favorite)));

        var response = objectUnderTest.getPagedFavoritesByUserId(user.getId(), 1, 20);

        assertEquals(1, response.getContent().size(), "Favorite page should contain repository results");
    }

    @Test
    void getPagedFavorites_whenAuthenticated_returnsOnlyCurrentUserFavorites() {
        var user = user(1L, "favorite-current@example.com");
        var category = category(1L, "Shirts");
        var item = item(1L, "Favorite Shirt", category);
        var size = size(1L, "M");
        var color = color(1L, "Blue", "#0000ff");
        var variant = itemVariant(1L, item, size, color);
        var favorite = favorite(1L, user, variant);
        Authentication authentication = authentication(user.getId());

        when(currentUserServiceMock.findCurrentUser(authentication)).thenReturn(user);
        when(favoriteRepositoryMock.findByUserId(org.mockito.ArgumentMatchers.eq(user.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(favorite)));

        var response = objectUnderTest.getPagedFavorites(authentication, 1, 20);

        assertEquals(user.getId(), response.getContent().get(0).getUserId(), "Favorites should be scoped to the current user");
    }

    @Test
    void updateFavorite_whenFavoriteExists_returnsUpdatedFavorite() {
        var user = user(1L, "favorite-service@example.com");
        var category = category(1L, "Shirts");
        var item = item(1L, "Favorite Shirt", category);
        var size = size(1L, "M");
        var color = color(1L, "Blue", "#0000ff");
        var variant = itemVariant(1L, item, size, color);
        var favorite = favorite(1L, user, variant);

        when(favoriteRepositoryMock.findById(1L)).thenReturn(Optional.of(favorite));
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemVariantRepositoryMock.findById(variant.getId())).thenReturn(Optional.of(variant));
        when(favoriteRepositoryMock.save(favorite)).thenReturn(favorite);

        FavoriteResponse response = objectUnderTest.updateFavorite(1L, favoriteRequest(user.getId(), variant.getId()));

        assertEquals(1L, response.getId(), "Updated favorite id should match saved entity");
    }

    @Test
    void deleteFavorite_whenFavoriteExists_deletesFavorite() {
        when(favoriteRepositoryMock.existsById(1L)).thenReturn(true);

        objectUnderTest.deleteFavorite(1L);

        verify(favoriteRepositoryMock).deleteById(1L);
    }

    @Test
    void deleteFavorite_whenAuthenticatedUserDoesNotOwnFavorite_throwsNotFoundException() {
        var user = user(1L, "favorite-owner@example.com");
        Authentication authentication = authentication(user.getId());
        when(currentUserServiceMock.findCurrentUser(authentication)).thenReturn(user);
        when(favoriteRepositoryMock.findByIdAndUserId(1L, user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> objectUnderTest.deleteFavorite(authentication, 1L));
    }

    @Test
    void getFavoriteById_whenFavoriteIsMissing_throwsNotFoundException() {
        when(favoriteRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> objectUnderTest.getFavoriteById(1L));
    }

    private Authentication authentication(Long userId) {
        return new TestingAuthenticationToken("user-" + userId + "@example.com", null);
    }
}
