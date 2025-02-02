package com.delivery.delivery_app.dto.order;

import com.delivery.delivery_app.constant.OrderStatus;
import com.delivery.delivery_app.constant.OrderType;
import com.delivery.delivery_app.constant.PaymentMethod;
import com.delivery.delivery_app.dto.user.DriverResponse;
import com.delivery.delivery_app.dto.user.UserDataResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderPreviewResponse {
    String id;

    OrderStatus status;

    Instant createdAt;

    Integer cost;

    OrderType orderType;

    List<OrderLocationResponse> locations;

    List<FoodOrderItemResponse> foodOrderItems;

    @Data
    public static class FoodOrderItemResponse {
        String food;
        Integer quantity;
        Integer total;
        String note;
    }
}
