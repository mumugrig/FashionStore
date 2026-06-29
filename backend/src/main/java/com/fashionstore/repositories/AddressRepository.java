package com.fashionstore.repositories;

import com.fashionstore.models.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>, JpaSpecificationExecutor<Address> {
    List<Address> findByUsersId(Long userId);
    Page<Address> findByUsersId(Long userId, Pageable pageable);
    Optional<Address> findByIdAndUsersId(Long id, Long userId);
    Optional<Address> findByCountryAndRegionAndCityAndPostalCodeAndAddressLine(
            String country,
            String region,
            String city,
            int postalCode,
            String addressLine);

    @Modifying
    @Query(value = "delete from user_addresses where user_id = :userId", nativeQuery = true)
    void deleteLinksByUserId(Long userId);
}
