package com.fashionstore.services;

import com.fashionstore.dto.request.FavoriteRequest;
import com.fashionstore.dto.response.FavoriteResponse;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.models.Favorite;
import com.fashionstore.repositories.FavoriteRepository;
import com.fashionstore.repositories.ItemVariantRepository;
import com.fashionstore.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ItemVariantRepository itemVariantRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, UserRepository userRepository, ItemVariantRepository itemVariantRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.itemVariantRepository = itemVariantRepository;
    }

    @Transactional
    public FavoriteResponse addFavorite(FavoriteRequest favoriteRequest) {
        Favorite favorite = new Favorite();
        favorite.setUser(userRepository.findById(favoriteRequest.getUserId())
                .orElseThrow(() -> new NotFoundException("User", favoriteRequest.getUserId())));
        favorite.setItemVariant(itemVariantRepository.findById(favoriteRequest.getItemVariantId())
                .orElseThrow(() -> new NotFoundException("ItemVariant", favoriteRequest.getItemVariantId())));
        Favorite savedFavorite = favoriteRepository.save(favorite);
        return FavoriteResponse.from(savedFavorite);
    }

    @Transactional
    public FavoriteResponse updateFavorite(Long id, FavoriteRequest favoriteRequest) {
        return favoriteRepository.findById(id).map(favorite -> {
            favorite.setUser(userRepository.findById(favoriteRequest.getUserId())
                    .orElseThrow(() -> new NotFoundException("User", favoriteRequest.getUserId())));
            favorite.setItemVariant(itemVariantRepository.findById(favoriteRequest.getItemVariantId())
                    .orElseThrow(() -> new NotFoundException("ItemVariant", favoriteRequest.getItemVariantId())));
            Favorite savedFavorite = favoriteRepository.save(favorite);
            return FavoriteResponse.from(savedFavorite);
        }).orElseThrow(() -> new NotFoundException("Favorite", id));
    }

    @Transactional(readOnly = true)
    public FavoriteResponse getFavoriteById(Long id) {
        return favoriteRepository.findById(id).map(FavoriteResponse::from).orElseThrow(() -> new NotFoundException("Favorite", id));
    }

    @Transactional(readOnly = true)
    public List<FavoriteResponse> getFavoriteByUserId(Long userId){
        return favoriteRepository.findByUserId(userId)
                .stream()
                .map(FavoriteResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FavoriteResponse> getAllFavorites() {
        return favoriteRepository.findAll()
                .stream()
                .map(FavoriteResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteFavorite(Long id){
        if (!favoriteRepository.existsById(id)) {
            throw new NotFoundException("Favorite", id);
        }
        favoriteRepository.deleteById(id);
    }
}
