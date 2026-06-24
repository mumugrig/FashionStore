package com.fashionstore.repositories;

import com.fashionstore.models.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    boolean existsByItemVariantId(Long itemVariantId);
    boolean existsByItemVariantItemId(Long itemId);
    void deleteByUserId(Long userId);
}

