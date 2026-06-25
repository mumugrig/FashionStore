package com.fashionstore.repositories;

import com.fashionstore.models.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByItemVariantId(Long itemVariantId);
    Page<Review> findByItemVariantItemId(Long itemId, Pageable pageable);
    Optional<Review> findByIdAndUserId(Long id, Long userId);
    boolean existsByItemVariantId(Long itemVariantId);
    boolean existsByItemVariantItemId(Long itemId);
    void deleteByUserId(Long userId);
}

