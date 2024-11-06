package com.delivery.delivery_app.constant;

public enum OrderStatus {
    PENDING,                 // Đơn hàng đang chờ tài xế
    WAITING_FOR_ACCEPTANCE,   // Đã tìm thấy tài xế, đang chờ tài xế chấp nhận
    ARRIVING,                 // Tài xế đang đến gần
    IN_PROGRESS,              // Đơn hàng đang được thực hiện
    COMPLETED,                // Đơn hàng đã hoàn thành
    CANCELED;                // Đơn hàng đã bị hủy
}
