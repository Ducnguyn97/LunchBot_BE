package vn.codegym.lunchbot_be.model;

import vn.codegym.lunchbot_be.model.enums.ShippingPartnerStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shipping_partners")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "orders")
public class ShippingPartner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    private String address;

    @Column(precision = 5, scale = 4)
    private BigDecimal commissionRate; // % per order

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShippingPartnerStatus status = ShippingPartnerStatus.ACTIVE;

    @Column(nullable = false)
    private Boolean isLocked = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "shippingPartner")
    private List<Order> orders = new ArrayList<>();
}