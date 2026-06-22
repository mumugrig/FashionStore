package com.fashionstore.services;

import com.fashionstore.dto.request.FavoriteRequest;
import com.fashionstore.dto.response.FavoriteResponse;
import com.fashionstore.models.Favorite;
import com.fashionstore.repositories.FavoriteRepository;
import com.fashionstore.repositories.ItemVariantRepository;
import com.fashionstore.repositories.UserRepository;
import org.springframework.stereotype.Service;

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

    public FavoriteResponse addFavorite(FavoriteRequest favoriteRequest) {
        Favorite favorite = new Favorite();
        userRepository.findById(favoriteRequest.getUserId()).ifPresent(favorite::setUser);
        itemVariantRepository.findById(favoriteRequest.getItemVariantId()).ifPresent(favorite::setItemVariant);
        Favorite savedFavorite = favoriteRepository.save(favorite);
        return FavoriteResponse.from(savedFavorite);
    }

    public FavoriteResponse updateFavorite(Long id, FavoriteRequest favoriteRequest) {
        return favoriteRepository.findById(id).map(favorite -> {
            userRepository.findById(favoriteRequest.getUserId()).ifPresent(favorite::setUser);
            itemVariantRepository.findById(favoriteRequest.getItemVariantId()).ifPresent(favorite::setItemVariant);
            Favorite savedFavorite = favoriteRepository.save(favorite);
            return FavoriteResponse.from(savedFavorite);
        }).orElse(null);
    }

    public FavoriteResponse getFavoriteById(Long id) {
        return favoriteRepository.findById(id).map(FavoriteResponse::from).orElse(null);
    }

    public List<FavoriteResponse> getAllFavorites() {
        return favoriteRepository.findAll()
                .stream()
                .map(FavoriteResponse::from)
                .collect(Collectors.toList());
    }

    public void deleteFavorite(Long id){
        favoriteRepository.deleteById(id);
    }
}