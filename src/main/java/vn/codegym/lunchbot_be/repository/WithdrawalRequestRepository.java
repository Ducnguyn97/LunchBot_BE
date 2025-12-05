package vn.codegym.lunchbot_be.repository;

import vn.codegym.lunchbot_be.model.WithdrawalRequest;
import vn.codegym.lunchbot_be.model.enums.WithdrawalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, Long> {
    List<WithdrawalRequest> findByMerchantId(Long merchantId);

    List<WithdrawalRequest> findByStatus(WithdrawalStatus status);

    Long countByStatus(WithdrawalStatus status);
}
