package vn.codegym.lunchbot_be.model.enums;

public enum OrderStatus {
    PENDING,        // Chờ nhận hàng
    CONFIRMED,      // Đã xác nhận (nhà hàng nhận đơn)
    PROCESSING,     // Đang chế biến
    READY,          // Đã nhận món (tài xế nhận món)
    DELIVERING,     // Đang giao
    COMPLETED,      // Đã hoàn thành
    CANCELLED       // Hủy
}
