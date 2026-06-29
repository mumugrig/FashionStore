package com.fashionstore.repositories;

import com.fashionstore.models.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long>, JpaSpecificationExecutor<CartItem> {
    List<CartItem> findByUserId(Long userId);
    Page<CartItem> findByUserId(Long userId, Pageable pageable);
    Optional<CartItem> findByIdAndUserId(Long id, Long userId);
    boolean existsByUserIdAndItemVariantId(Long userId, Long itemVariantId);
    boolean existsByUserIdAndItemVariantIdAndIdNot(Long userId, Long itemVariantId, Long id);
    boolean existsByItemVariantId(Long itemVariantId);
    boolean existsByItemVariantItemId(Long itemId);
    void deleteByUserId(Long userId);
}

