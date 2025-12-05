package vn.codegym.lunchbot_be.repository;

import vn.codegym.lunchbot_be.model.Transaction;
import vn.codegym.lunchbot_be.model.enums.TransactionStatus;
import vn.codegym.lunchbot_be.model.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByMerchantId(Long merchantId);

    List<Transaction> findByMerchantIdAndTransactionType(Long merchantId, TransactionType type);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.merchant.id = :merchantId " +
            "AND t.transactionType = :type AND t.status = 'COMPLETED'")
    BigDecimal sumAmountByMerchantAndType(@Param("merchantId") Long merchantId,
                                          @Param("type") TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findBetweenDates(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);
}