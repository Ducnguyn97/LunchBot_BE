package vn.codegym.lunchbot_be.repository;

import vn.codegym.lunchbot_be.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);

    Optional<Address> findByUserIdAndIsDefault(Long userId, Boolean isDefault);

    Long countByUserId(Long userId);
}
