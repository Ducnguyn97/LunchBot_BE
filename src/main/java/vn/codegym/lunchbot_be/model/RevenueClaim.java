package vn.codegym.lunchbot_be.model;

import vn.codegym.lunchbot_be.model.enums.ClaimStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "revenue_claims")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "merchant")
public class RevenueClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 12, scale = 2)
    private BigDecimal disputedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status = ClaimStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String adminResponse;

    @CreationTimestamp
    private LocalDateTime claimedAt;

    private LocalDateTime resolvedAt;

    // Business methods
    public void review(String response) {
        this.status = ClaimStatus.REVIEWED;
        this.adminResponse = response;
    }

    public void resolve() {
        this.status = ClaimStatus.RESOLVED;
        this.resolvedAt = LocalDateTime.now();
    }
}
