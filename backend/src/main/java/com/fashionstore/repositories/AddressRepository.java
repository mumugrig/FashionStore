package com.fashionstore.repositories;

import com.fashionstore.models.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);
    Page<Address> findByUserId(Long userId, Pageable pageable);
    Optional<Address> findByIdAndUserId(Long id, Long userId);
    void deleteByUserId(Long userId);
}
