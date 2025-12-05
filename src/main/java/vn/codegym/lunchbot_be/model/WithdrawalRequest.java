package vn.codegym.lunchbot_be.model;

import vn.codegym.lunchbot_be.model.enums.WithdrawalStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "withdrawal_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "merchant")
public class WithdrawalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WithdrawalStatus status = WithdrawalStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String adminNotes;

    @CreationTimestamp
    private LocalDateTime requestedAt;

    private LocalDateTime processedAt;

    // Business methods
    public void approve(String notes) {
        this.status = WithdrawalStatus.APPROVED;
        this.adminNotes = notes;
        this.processedAt = LocalDateTime.now();
    }

    public void reject(String notes) {
        this.status = WithdrawalStatus.REJECTED;
        this.adminNotes = notes;
        this.processedAt = LocalDateTime.now();
    }
}
