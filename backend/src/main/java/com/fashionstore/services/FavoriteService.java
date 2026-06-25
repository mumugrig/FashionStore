package com.fashionstore.services;

import com.fashionstore.dto.request.FavoriteRequest;
import com.fashionstore.dto.response.FavoriteResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.models.Favorite;
import com.fashionstore.repositories.FavoriteRepository;
import com.fashionstore.repositories.ItemVariantRepository;
import com.fashionstore.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ItemVariantRepository itemVariantRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public FavoriteResponse addFavorite(FavoriteRequest favoriteRequest) {
        Favorite favorite = new Favorite();
        favorite.setUser(userRepository.findById(favoriteRequest.getUserId())
                .orElseThrow(() -> new NotFoundException("User", favoriteRequest.getUserId())));
        applyFavoriteRequest(favorite, favoriteRequest);
        Favorite savedFavorite = favoriteRepository.save(favorite);
        return FavoriteResponse.from(savedFavorite);
    }

    @Transactional
    public FavoriteResponse addFavorite(Authentication authentication, FavoriteRequest favoriteRequest) {
        Favorite favorite = new Favorite();
        favorite.setUser(currentUserService.findCurrentUser(authentication));
        applyFavoriteRequest(favorite, favoriteRequest);
        Favorite savedFavorite = favoriteRepository.save(favorite);
        return FavoriteResponse.from(savedFavorite);
    }

    @Transactional
    public FavoriteResponse updateFavorite(Long id, FavoriteRequest favoriteRequest) {
        return favoriteRepository.findById(id).map(favorite -> {
            favorite.setUser(userRepository.findById(favoriteRequest.getUserId())
                    .orElseThrow(() -> new NotFoundException("User", favoriteRequest.getUserId())));
            applyFavoriteRequest(favorite, favoriteRequest);
            Favorite savedFavorite = favoriteRepository.save(favorite);
            return FavoriteResponse.from(savedFavorite);
        }).orElseThrow(() -> new NotFoundException("Favorite", id));
    }

    @Transactional(readOnly = true)
    public FavoriteResponse getFavoriteById(Long id) {
        return favoriteRepository.findById(id).map(FavoriteResponse::from).orElseThrow(() -> new NotFoundException("Favorite", id));
    }

    @Transactional(readOnly = true)
    public PageResponse<FavoriteResponse> getPagedFavoritesByUserId(Long userId, int page, int size){
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User", userId);
        }
        return PageResponse.from(favoriteRepository.findByUserId(userId, PageRequestFactory.create(page, size)), FavoriteResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<FavoriteResponse> getPagedFavorites(Authentication authentication, int page, int size) {
        var currentUser = currentUserService.findCurrentUser(authentication);
        return PageResponse.from(favoriteRepository.findByUserId(currentUser.getId(), PageRequestFactory.create(page, size)), FavoriteResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<FavoriteResponse> getPagedFavorites(int page, int size) {
        return PageResponse.from(favoriteRepository.findAll(PageRequestFactory.create(page, size)), FavoriteResponse::from);
    }

    @Transactional
    public void deleteFavorite(Long id){
        if (!favoriteRepository.existsById(id)) {
            throw new NotFoundException("Favorite", id);
        }
        favoriteRepository.deleteById(id);
    }

    @Transactional
    public void deleteFavorite(Authentication authentication, Long id){
        var currentUser = currentUserService.findCurrentUser(authentication);
        Favorite favorite = favoriteRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new NotFoundException("Favorite", id));
        favoriteRepository.delete(favorite);
    }

    private void applyFavoriteRequest(Favorite favorite, FavoriteRequest favoriteRequest) {
        favorite.setItemVariant(itemVariantRepository.findById(favoriteRequest.getItemVariantId())
                .orElseThrow(() -> new NotFoundException("ItemVariant", favoriteRequest.getItemVariantId())));
    }
}
