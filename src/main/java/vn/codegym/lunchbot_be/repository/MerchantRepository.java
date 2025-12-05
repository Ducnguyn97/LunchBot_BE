package vn.codegym.lunchbot_be.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.codegym.lunchbot_be.model.Merchant;

import java.util.List;
import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    Optional<Merchant> findByUserId(Long userId);
    Optional<Merchant> findByPhone(String phone);

    @Query("SELECT m FROM Merchant m WHERE m.restaurantName LIKE %:keyword%")
    Page<Merchant> searchByName(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT m FROM Merchant m WHERE m.isPartner = :isPartner")
    List<Merchant> findByPartnerStatus(@Param("isPartner") boolean isPartner);
}
