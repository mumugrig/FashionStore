package com.fashionstore.services;

import com.fashionstore.dto.request.FavoriteRequest;
import com.fashionstore.dto.response.AdminFavoriteResponse;
import com.fashionstore.dto.response.FavoriteResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.models.Favorite;
import com.fashionstore.repositories.FavoriteRepository;
import com.fashionstore.repositories.ItemVariantRepository;
import com.fashionstore.repositories.UserRepository;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


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
    public AdminFavoriteResponse getAdminFavoriteById(Long id) {
        return favoriteRepository.findById(id)
                .map(AdminFavoriteResponse::from)
                .orElseThrow(() -> new NotFoundException("Favorite", id));
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
        return getPagedFavorites(authentication, page, size, null);
    }

    @Transactional(readOnly = true)
    public PageResponse<FavoriteResponse> getPagedFavorites(Authentication authentication, int page, int size, String search) {
        var currentUser = currentUserService.findCurrentUser(authentication);
        if (!hasText(search)) {
            return PageResponse.from(favoriteRepository.findByUserId(currentUser.getId(), PageRequestFactory.create(page, size)), FavoriteResponse::from);
        }
        return PageResponse.from(favoriteRepository.findAll(
                favoriteSearch(currentUser.getId(), search),
                PageRequestFactory.create(page, size)
        ), FavoriteResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<FavoriteResponse> getPagedFavorites(int page, int size) {
        return PageResponse.from(favoriteRepository.findAll(PageRequestFactory.create(page, size)), FavoriteResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<FavoriteResponse> getPagedFavorites(int page, int size, String search, String filterColumn, String filterValue) {
        if (!AdminFilterSpecification.hasFilters(search, filterColumn, filterValue)) {
            return getPagedFavorites(page, size);
        }
        return PageResponse.from(favoriteRepository.findAll(
                AdminFilterSpecification.create(adminFields(), search, filterColumn, filterValue),
                PageRequestFactory.create(page, size)
        ), FavoriteResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminFavoriteResponse> getPagedAdminFavorites(int page, int size, String search, String filterColumn, String filterValue) {
        if (!AdminFilterSpecification.hasFilters(search, filterColumn, filterValue)) {
            return PageResponse.from(favoriteRepository.findAll(PageRequestFactory.create(page, size)), AdminFavoriteResponse::from);
        }
        return PageResponse.from(favoriteRepository.findAll(
                AdminFilterSpecification.create(adminFields(), search, filterColumn, filterValue),
                PageRequestFactory.create(page, size)
        ), AdminFavoriteResponse::from);
    }

    @Transactional
    public void deleteFavorite(Long id){
        if (!favoriteRepository.existsById(id)) {
            throw new NotFoundException("Favorite", id);
        }
        favoriteRepository.deleteById(id);
    }

    @Transactional
    public void deleteFavorites(List<Long> ids) {
        ids.forEach(this::deleteFavorite);
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

    private Specification<Favorite> favoriteSearch(Long userId, String search) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            var item = root.join("itemVariant").join("item");
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("user").get("id"), userId),
                    criteriaBuilder.like(criteriaBuilder.lower(item.get("name")), "%" + normalized(search) + "%")
            );
        };
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String normalized(String value) {
        return value.trim().toLowerCase();
    }

    private Map<String, Function<Root<Favorite>, Expression<?>>> adminFields() {
        return Map.ofEntries(
                Map.entry("id", root -> root.get("id")),
                Map.entry("itemVariantId", root -> root.get("itemVariant").get("id")),
                Map.entry("userId", root -> root.get("user").get("id")),
                Map.entry("userFirstName", root -> root.get("user").get("firstName")),
                Map.entry("userLastName", root -> root.get("user").get("lastName")),
                Map.entry("userName", root -> root.get("user").get("firstName")),
                Map.entry("userEmail", root -> root.get("user").get("email")),
                Map.entry("itemId", root -> root.get("itemVariant").get("item").get("id")),
                Map.entry("itemName", root -> root.get("itemVariant").get("item").get("name")),
                Map.entry("sizeLabel", root -> root.get("itemVariant").get("size").get("label")),
                Map.entry("sizeSystem", root -> root.get("itemVariant").get("size").get("sizeSystem")),
                Map.entry("colorName", root -> root.get("itemVariant").get("color").get("name")),
                Map.entry("colorValue", root -> root.get("itemVariant").get("color").get("value")),
                Map.entry("variantActive", root -> root.get("itemVariant").get("isActive")),
                Map.entry("variantStockLeft", root -> root.get("itemVariant").get("stockLeft"))
        );
    }
}
