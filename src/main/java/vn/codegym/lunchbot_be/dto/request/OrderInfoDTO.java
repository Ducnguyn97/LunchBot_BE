package vn.codegym.lunchbot_be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfoDTO {
    /**
     * Danh sách ID món ăn được chọn
     */
    private List<Long> items;

    /**
     * ID địa chỉ giao hàng
     */
    private Long addressId;

    /**
     * Tổng số tiền thanh toán
     */
    private BigDecimal amount;

    /**
     * Tên merchant (nhà hàng)
     */
    private String merchantName;

    /**
     * Email người dùng (để tạo đơn hàng trong IPN)
     */
    private String userEmail;

    /**
     * Mã coupon (nếu có)
     */
    private String couponCode;

    /**
     * Ghi chú đơn hàng
     */
    private String notes;

    /**
     * Phí giao hàng
     */
    private BigDecimal shippingFee;
}
