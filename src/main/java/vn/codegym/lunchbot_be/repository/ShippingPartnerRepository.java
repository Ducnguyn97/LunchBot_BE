package vn.codegym.lunchbot_be.repository;

import vn.codegym.lunchbot_be.model.ShippingPartner;
import vn.codegym.lunchbot_be.model.enums.ShippingPartnerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShippingPartnerRepository extends JpaRepository<ShippingPartner, Long> {
    List<ShippingPartner> findByStatus(ShippingPartnerStatus status);

    @Query("SELECT sp FROM ShippingPartner sp WHERE sp.isLocked = false AND sp.status = 'ACTIVE'")
    List<ShippingPartner> findActiveAndUnlocked();

    Long countByStatus(ShippingPartnerStatus status);
}
