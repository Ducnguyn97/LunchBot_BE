package vn.codegym.lunchbot_be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.codegym.lunchbot_be.dto.request.CheckoutRequest;
import vn.codegym.lunchbot_be.dto.request.OrderInfoDTO;
import vn.codegym.lunchbot_be.model.Order;
import vn.codegym.lunchbot_be.model.enums.PaymentMethod;
import vn.codegym.lunchbot_be.model.enums.PaymentStatus;
import vn.codegym.lunchbot_be.repository.OrderRepository;
import vn.codegym.lunchbot_be.service.OrderService;
import vn.codegym.lunchbot_be.service.impl.VNPayServiceImpl;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class VNPayController {

    private final VNPayServiceImpl vnPayService;

    private final OrderService orderService;

    private final OrderRepository orderRepository;

    @GetMapping("/create-payment")
    public ResponseEntity<?> createPayment(
            @RequestParam("amount") long amount,
            @RequestParam("orderInfo") String orderInfo,
            HttpServletRequest request
    ) {
        try {
            String paymentUrl = vnPayService.createPaymentUrl(amount, orderInfo, request);
            return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Không thể tạo URL thanh toán: " + e.getMessage()));
        }
    }

    /**
     * IPN Endpoint - VNPay gọi về đây để xác nhận thanh toán
     * ⚠️ QUAN TRỌNG: Endpoint này PHẢI public, không được bảo vệ bởi authentication
     */
    @GetMapping("/vnpay/vnpay-ipn")
    public ResponseEntity<?> vnpayIPN(@RequestParam Map<String, String> params) {
        try {
            // Xác thực chữ ký
            boolean isValidSignature = vnPayService.verifySignature(params);
            if (!isValidSignature) {
                return ResponseEntity.ok(Map.of("RspCode", "97", "Message", "Invalid signature"));
            }

            String vnp_ResponseCode = params.get("vnp_ResponseCode");
            String vnp_TxnRef = params.get("vnp_TxnRef");
            String vnp_OrderInfo = params.get("vnp_OrderInfo");

            if ("00".equals(vnp_ResponseCode)) {
                // ✅ Parse orderInfo JSON
                ObjectMapper mapper = new ObjectMapper();
                OrderInfoDTO orderInfo = mapper.readValue(vnp_OrderInfo, OrderInfoDTO.class);

                // ✅ Tạo đơn hàng từ orderInfo
                CheckoutRequest request = new CheckoutRequest();
                request.setDishIds(orderInfo.getItems());
                request.setAddressId(orderInfo.getAddressId());
                request.setPaymentMethod(PaymentMethod.CARD);

                // Giả sử bạn có cách lấy email từ orderInfo hoặc token
                String userEmail = orderInfo.getUserEmail();

                Order order = orderService.createOrder(userEmail, request);

                order.setVnpayTransactionRef(vnp_TxnRef);
                order.setPaymentStatus(PaymentStatus.PAID);
                orderRepository.save(order);

                return ResponseEntity.ok(Map.of("RspCode", "00", "Message", "Confirm Success"));
            }

            return ResponseEntity.ok(Map.of("RspCode", "00", "Message", "Confirm Success"));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("RspCode", "99", "Message", "Unknown error"));
        }
    }
}
