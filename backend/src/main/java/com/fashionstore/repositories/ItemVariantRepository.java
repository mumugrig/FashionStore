package com.fashionstore.repositories;

import com.fashionstore.models.ItemVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemVariantRepository extends JpaRepository<ItemVariant, Long>, JpaSpecificationExecutor<ItemVariant> {
    List<ItemVariant> findByItemId(Long itemId);
    List<ItemVariant> findByItemIdAndIsActiveTrue(Long itemId);
    Optional<ItemVariant> findByIdAndIsActiveTrue(Long id);
    boolean existsBySizeId(Long sizeId);
    boolean existsByColorId(Long colorId);
}

